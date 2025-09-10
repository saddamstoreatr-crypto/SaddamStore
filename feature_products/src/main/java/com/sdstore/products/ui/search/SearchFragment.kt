package com.sdstore.products.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.sdstore.cart.viewmodels.CartViewModel
import com.sdstore.core.models.Sku
import com.sdstore.products.databinding.FragmentSearchBinding
import com.sdstore.products.ui.detail.ItemDetailDialog
import com.sdstore.products.ui.page.ProductAdapter
import com.sdstore.products.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()

        binding.etSearch.addTextChangedListener { text ->
            viewModel.searchProducts(text.toString())
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onCartAction = { sku, newQuantity ->
                if (newQuantity > 0) {
                    cartViewModel.updateItemQuantity(sku, newQuantity)
                } else {
                    cartViewModel.removeFromCart(sku)
                }
            },
            onItemClick = { sku ->
                showItemDetailDialog(sku)
            }
        )
        binding.rvSearchResults.layoutManager = GridLayoutManager(context, 2)
        binding.rvSearchResults.adapter = productAdapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.searchResults.collect { results ->
                        productAdapter.submitList(results)
                    }
                }

                launch {
                    cartViewModel.cartItems.collect { cartItems ->
                        productAdapter.updateCartItems(cartItems)
                    }
                }
            }
        }
    }

    private fun showItemDetailDialog(sku: Sku) {
        ItemDetailDialog.newInstance(sku).show(parentFragmentManager, "ItemDetailDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
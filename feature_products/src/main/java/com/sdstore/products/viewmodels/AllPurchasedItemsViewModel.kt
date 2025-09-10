package com.sdstore.products.viewmodels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.sdstore.R
import com.sdstore.cart.viewmodels.CartViewModel
import com.sdstore.core.viewmodels.UiState
import com.sdstore.products.databinding.FragmentAllPurchasedItemsBinding
import com.sdstore.products.ui.detail.ItemDetailDialog
import com.sdstore.products.ui.page.ProductAdapter
import com.sdstore.products.viewmodels.AllPurchasedItemsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllPurchasedItemsFragment : Fragment() {

    private var _binding: FragmentAllPurchasedItemsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AllPurchasedItemsViewModel by viewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAllPurchasedItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.title = getString(R.string.all_purchased_items)
        setupRecyclerView()
        setupObservers()

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.etSearch.addTextChangedListener { text ->
            viewModel.searchPurchasedItems(text.toString())
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onCartAction = { sku, newQuantity ->
                cartViewModel.updateItemQuantity(sku, newQuantity)
            },
            onItemClick = { sku ->
                ItemDetailDialog.newInstance(sku).show(parentFragmentManager, "ItemDetailDialog")
            }
        )
        binding.rvAllItems.layoutManager = GridLayoutManager(context, 2)
        binding.rvAllItems.adapter = productAdapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.itemsState.collect { state ->
                        binding.progressBar.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE

                        when (state) {
                            is UiState.Success -> {
                                val isSearchActive = binding.etSearch.text.toString().isNotEmpty()
                                if (state.data.isEmpty()) {
                                    binding.tvEmptyItems.visibility = View.VISIBLE
                                    binding.rvAllItems.visibility = View.GONE
                                    binding.tvEmptyItems.text = if (isSearchActive) {
                                        getString(R.string.no_search_results)
                                    } else {
                                        getString(R.string.no_purchased_items_found)
                                    }
                                } else {
                                    binding.tvEmptyItems.visibility = View.GONE
                                    binding.rvAllItems.visibility = View.VISIBLE
                                    productAdapter.submitList(state.data)
                                }
                            }
                            is UiState.Error -> {
                                binding.tvEmptyItems.text = state.message
                                binding.tvEmptyItems.visibility = View.VISIBLE
                                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                            }
                            else -> {}
                        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
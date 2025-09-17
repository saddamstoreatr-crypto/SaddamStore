package com.sdstore.products.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sdstore.cart.viewmodels.CartViewModel
import com.sdstore.feature_products.databinding.FragmentSearchBinding
import com.sdstore.products.ui.page.ProductAdapter
import com.sdstore.products.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()

    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()

        binding.searchBar.etSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.searchProducts(text.toString())
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            onItemClick = { /* Handle item click */ },
            onAddToCartClick = { sku -> cartViewModel.addToCart(sku) },
            onIncreaseClick = { sku -> cartViewModel.increaseQuantity(sku.id) },
            onDecreaseClick = { sku -> cartViewModel.decreaseQuantity(sku.id) }
        )
        binding.rvSearchResults.layoutManager = LinearLayoutManager(context)
        binding.rvSearchResults.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            adapter.submitList(results)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
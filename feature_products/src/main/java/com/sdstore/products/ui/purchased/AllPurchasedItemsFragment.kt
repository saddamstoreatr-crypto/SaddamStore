package com.sdstore.products.ui.purchased

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sdstore.cart.viewmodels.CartViewModel
import com.sdstore.feature_products.databinding.FragmentAllPurchasedItemsBinding
import com.sdstore.products.viewmodels.AllPurchasedItemsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllPurchasedItemsFragment : Fragment() {

    private var _binding: FragmentAllPurchasedItemsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AllPurchasedItemsViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()

    private lateinit var adapter: AllPurchasedItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllPurchasedItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = AllPurchasedItemsAdapter(
            onAddToCartClick = { sku -> cartViewModel.addToCart(sku) },
            onIncreaseClick = { sku -> cartViewModel.increaseQuantity(sku) },
            onDecreaseClick = { sku -> cartViewModel.decreaseQuantity(sku) }
        )
        binding.rvPurchasedItems.layoutManager = LinearLayoutManager(context)
        binding.rvPurchasedItems.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.itemsState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        // Show loading indicator
                    }
                    is UiState.Success -> {
                        val items = state.data
                        if (items.isNullOrEmpty()) {
                            binding.tvEmpty.visibility = View.VISIBLE
                            binding.rvPurchasedItems.visibility = View.GONE
                        } else {
                            binding.tvEmpty.visibility = View.GONE
                            binding.rvPurchasedItems.visibility = View.VISIBLE
                            adapter.submitList(items)
                        }
                    }
                    is UiState.Error -> {
                        // Show error message
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
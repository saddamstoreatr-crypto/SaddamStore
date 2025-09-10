package com.sdstore.products.ui.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sdstore.cart.viewmodels.CartViewModel
import com.sdstore.core.models.Category
import com.sdstore.core.viewmodels.UiState
import com.sdstore.products.databinding.FragmentPageBinding
import com.sdstore.products.ui.detail.ItemDetailDialog
import com.sdstore.products.viewmodels.PageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PageFragment : Fragment() {

    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PageViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    private lateinit var homePageAdapter: HomePageAdapter
    private lateinit var layoutManager: GridLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()

        binding.searchBar.etSearch.addTextChangedListener { text ->
            viewModel.searchProducts(text.toString())
        }
    }

    private fun setupRecyclerView() {
        homePageAdapter = HomePageAdapter(
            onCategoryClick = { category ->
                if (category.imageUrl == Category.PURCHASED_ITEMS_IMAGE_URL) {
                    findNavController().navigate(com.sdstore.products.R.id.action_pageFragment_to_allPurchasedItemsFragment)
                } else {
                    val action = PageFragmentDirections.actionPageFragmentToCategoryProductsFragment(category.name)
                    findNavController().navigate(action)
                }
            },
            onProductClick = { sku ->
                val dialog = ItemDetailDialog.newInstance(sku)
                dialog.show(parentFragmentManager, "ItemDetailDialog")
            },
            onCartAction = { sku, newQuantity ->
                if (newQuantity > 0) {
                    cartViewModel.updateItemQuantity(sku, newQuantity)
                } else {
                    cartViewModel.removeFromCart(sku)
                }
            }
        )

        layoutManager = GridLayoutManager(context, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position >= 0 && position < homePageAdapter.currentList.size) {
                    return when (homePageAdapter.getItemViewType(position)) {
                        ITEM_TYPE_PRODUCT -> 1
                        else -> 3
                    }
                }
                return 3
            }
        }

        binding.itemsListView.layoutManager = layoutManager
        binding.itemsListView.adapter = homePageAdapter

        binding.itemsListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (dy > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    viewModel.loadMoreProducts()
                }
            }
        })
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.homePageItemsState.collect { state ->
                        val isLoading = state is UiState.Loading

                        binding.shimmerViewContainer.isVisible = isLoading && homePageAdapter.currentList.isEmpty()
                        if (isLoading) {
                            binding.shimmerViewContainer.startShimmer()
                        } else {
                            binding.shimmerViewContainer.stopShimmer()
                        }

                        binding.itemsListView.isVisible = state is UiState.Success && state.data.isNotEmpty()
                        binding.errorView.errorViewContainer.isVisible = state is UiState.Error

                        when (state) {
                            is UiState.Success -> {
                                homePageAdapter.submitList(state.data)
                            }
                            is UiState.Error -> {
                                binding.errorView.tvErrorMessageFull.text = state.message
                                binding.errorView.btnRetryFull.setOnClickListener { viewModel.loadMoreProducts() }
                            }
                            else -> { /* Loading/Idle state handled by visibility changes above */ }
                        }
                    }
                }

                launch {
                    cartViewModel.cartItems.collect { cartItems ->
                        homePageAdapter.updateCartItems(cartItems)
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
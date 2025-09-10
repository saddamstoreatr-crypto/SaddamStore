package com.sdstore.products.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sdstore.R
import com.sdstore.cart.viewmodels.CartViewModel
import com.sdstore.core.viewmodels.UiState
import com.sdstore.products.databinding.FragmentCategoryProductsBinding
import com.sdstore.products.ui.detail.ItemDetailDialog
import com.sdstore.products.ui.page.ProductAdapter
import com.sdstore.products.viewmodels.PageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryProductsFragment : Fragment() {

    private var _binding: FragmentCategoryProductsBinding? = null
    private val binding get() = _binding!!

    private val args: CategoryProductsFragmentArgs by navArgs()
    private val viewModel: PageViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private lateinit var productAdapter: ProductAdapter
    private lateinit var layoutManager: GridLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.title = args.categoryName
        setupRecyclerView()
        setupObservers()

        if (args.categoryName == getString(R.string.buy_again_title)) {
            findNavController().navigate(com.sdstore.products.R.id.action_categoryProductsFragment_to_allPurchasedItemsFragment)
        } else {
            viewModel.fetchProductsByCategory(args.categoryName, isInitialLoad = true)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
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
        layoutManager = GridLayoutManager(context, 2)
        binding.rvCategoryProducts.layoutManager = layoutManager
        binding.rvCategoryProducts.adapter = productAdapter

        binding.rvCategoryProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        viewModel.fetchProductsByCategory(args.categoryName)
                    }
                }
            }
        })
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.categoryProductsState.collect { state ->
                        binding.progressBar.visibility = if (state is UiState.Loading && productAdapter.itemCount == 0) View.VISIBLE else View.GONE

                        when (state) {
                            is UiState.Success -> {
                                binding.tvEmptyCategory.visibility = if (state.data.isEmpty()) View.VISIBLE else View.GONE
                                productAdapter.submitList(state.data)
                            }
                            is UiState.Error -> {
                                binding.tvEmptyCategory.text = state.message
                                binding.tvEmptyCategory.visibility = View.VISIBLE
                                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                            }
                            else -> { /* Loading state handled above */ }
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
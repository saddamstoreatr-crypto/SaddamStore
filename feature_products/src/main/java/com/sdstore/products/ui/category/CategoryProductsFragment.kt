package com.sdstore.products.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.sdstore.cart.viewmodels.CartViewModel
import com.sdstore.feature_products.databinding.FragmentCategoryProductsBinding
import com.sdstore.products.ui.page.ProductAdapter
import com.sdstore.products.viewmodels.PageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryProductsFragment : Fragment() {

    private var _binding: FragmentCategoryProductsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PageViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()
    private val args: CategoryProductsFragmentArgs by navArgs()

    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        viewModel.getProductsByCategory(args.categoryId)
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            onItemClick = { sku ->
                // Handle item click
            },
            onAddToCartClick = { sku ->
                cartViewModel.addToCart(sku)
            },
            onIncreaseClick = { sku ->
                cartViewModel.increaseQuantity(sku.id)
            },
            onDecreaseClick = { sku ->
                cartViewModel.decreaseQuantity(sku.id)
            }
        )
        binding.rvCategoryProducts.layoutManager = GridLayoutManager(context, 2)
        binding.rvCategoryProducts.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            binding.tvEmptyCategory.isVisible = products.isNullOrEmpty()
            binding.rvCategoryProducts.isVisible = !products.isNullOrEmpty()
            adapter.submitList(products)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.sdstore.products.ui.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sdstore.cart.viewmodels.CartViewModel
import com.sdstore.feature_products.databinding.FragmentPageBinding
import com.sdstore.products.viewmodels.PageViewModel
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PageFragment : Fragment() {

    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PageViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()

    private lateinit var homePageAdapter: HomePageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        // homePageAdapter = HomePageAdapter(
        //     onBannerClick = { /* Handle banner click */ },
        //     onCategoryClick = { /* Handle category click */ },
        //     onProductClick = { /* Handle product click */ },
        //     onAddToCartClick = { sku -> cartViewModel.addToCart(sku) },
        //     onIncreaseClick = { sku -> cartViewModel.increaseQuantity(sku.id) },
        //     onDecreaseClick = { sku -> cartViewModel.decreaseQuantity(sku.id) },
        //     onViewAllClick = { /* Handle view all click */ }
        // )
        // binding.rvHomePage.layoutManager = LinearLayoutManager(context)
        // binding.rvHomePage.adapter = homePageAdapter
    }

    private fun observeViewModel() {
        // viewModel.homePageItems.observe(viewLifecycleOwner) { items ->
        //     homePageAdapter.submitList(items)
        // }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
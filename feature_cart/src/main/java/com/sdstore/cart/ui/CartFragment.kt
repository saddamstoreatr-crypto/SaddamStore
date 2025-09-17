package com.sdstore.cart.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sdstore.cart.viewmodels.CartViewModel
import com.sdstore.core.data.Result
import com.sdstore.core.models.CartItem
import com.sdstore.feature_cart.databinding.FragmentCartBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment(), CartItemListener {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeCartState()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(this)
        binding.rvCartItems.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeCartState() {
        viewModel.cartState.observe(viewLifecycleOwner) { result ->
            when (result) {
                // Fix: Result.Loading ab sahi se access hoga.
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.groupCartContent.visibility = View.GONE
                }
                is Result.Success<List<CartItem>> -> {
                    binding.progressBar.visibility = View.GONE
                    val cartList = result.data
                    if (cartList.isNullOrEmpty()) {
                        binding.groupCartContent.visibility = View.GONE
                    } else {
                        // Fix: groupCartContent ab theek kaam karega.
                        binding.groupCartContent.visibility = View.VISIBLE
                        cartAdapter.submitList(cartList)
                        // Fix: price ab sahi se access hoga.
                        val totalPrice = cartList.sumOf { (it.sku.price) * it.quantity }
                        binding.tvTotalPrice.text = "Total: Rs $totalPrice"
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.groupCartContent.visibility = View.GONE
                }
            }
        }
    }

    override fun onIncreaseQuantity(item: CartItem) {
        viewModel.updateQuantity(item, item.quantity + 1)
    }

    override fun onDecreaseQuantity(item: CartItem) {
        if (item.quantity > 1) {
            viewModel.updateQuantity(item, item.quantity - 1)
        }
    }

    override fun onRemoveItem(item: CartItem) {
        viewModel.removeItem(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
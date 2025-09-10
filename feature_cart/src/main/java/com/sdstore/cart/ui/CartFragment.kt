package com.sdstore.cart.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sdstore.R
import com.sdstore.cart.databinding.FragmentCartBinding
import com.sdstore.cart.viewmodels.CartViewModel
import com.sdstore.core.utils.Event
import com.sdstore.core.viewmodels.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val cartViewModel: CartViewModel by activityViewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        cartViewModel.refreshCart()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChange = { sku, newQuantity ->
                cartViewModel.updateItemQuantity(sku, newQuantity)
            },
            onRemoveItem = { sku ->
                cartViewModel.removeFromCart(sku)
            }
        )
        binding.rvCartItems.adapter = cartAdapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    cartViewModel.cartItems.collect { items ->
                        binding.tvEmptyCart.isVisible = items.isEmpty()
                        binding.bottomCard.isVisible = items.isNotEmpty()
                        cartAdapter.submitList(items)
                    }
                }

                launch {
                    cartViewModel.totalPrice.collect { price ->
                        val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
                        binding.tvTotalPrice.text = format.format(price / 100.0)
                    }
                }

                launch {
                    cartViewModel.orderPlacementState.collect { state ->
                        val isLoading = state is UiState.Loading
                        binding.placeOrderProgress.isVisible = isLoading
                        binding.btnPlaceOrder.text = if (isLoading) "" else getString(R.string.place_order)
                        binding.btnPlaceOrder.isEnabled = !isLoading

                        when (state) {
                            is UiState.Success -> {
                                Snackbar.make(binding.root, getString(R.string.order_placed_successfully), Snackbar.LENGTH_LONG).show()
                                // Note: Navigation action ID needs to be globally unique or defined in a common graph
                                findNavController().navigate(R.id.deliveriesFragment)
                                cartViewModel.resetOrderPlacementState()
                            }
                            is UiState.Error -> {
                                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                                cartViewModel.resetOrderPlacementState()
                            }
                            else -> { /* Do nothing for Idle or Loading */ }
                        }
                    }
                }

                launch {
                    cartViewModel.cartUpdateError.collect { event ->
                        event?.getContentIfNotHandled()?.let { errorMessage ->
                            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnPlaceOrder.setOnClickListener {
            cartViewModel.placeOrder()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
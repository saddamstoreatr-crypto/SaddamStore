package com.sdstore.cart.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdstore.core.data.Result
import com.sdstore.core.data.repository.CartRepository
import com.sdstore.core.models.CartItem
import com.sdstore.core.models.Sku
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _cartState = MutableLiveData<Result<List<CartItem>>>()
    val cartState: LiveData<Result<List<CartItem>>> = _cartState

    init {
        getCartItems()
    }

    private fun getCartItems() {
        // Fix: Result.Loading ab sahi se access hoga.
        _cartState.value = Result.Loading
        viewModelScope.launch {
            _cartState.postValue(cartRepository.getCartItems())
        }
    }

    fun updateQuantity(item: CartItem, newQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(item, newQuantity)
            getCartItems()
        }
    }

    fun removeItem(item: CartItem) {
        viewModelScope.launch {
            cartRepository.removeItem(item)
            getCartItems()
        }
    }

    fun placeOrder() {
        // Placeholder for place order logic
    }

    fun addToCart(sku: Sku) {
        viewModelScope.launch {
            val currentCart = (_cartState.value as? Result.Success)?.data ?: emptyList()
            val existingItem = currentCart.find { it.sku.id == sku.id }

            if (existingItem != null) {
                // Item already in cart, maybe increase quantity?
                // For now, we do nothing to avoid confusion.
                // A toast message could be shown here.
            } else {
                val cartItem = CartItem(sku = sku, quantity = 1)
                cartRepository.addItem(cartItem)
            }
            getCartItems()
        }
    }

    fun increaseQuantity(sku: Sku) {
        viewModelScope.launch {
            val currentCart = (_cartState.value as? Result.Success)?.data ?: return@launch
            val item = currentCart.find { it.sku.id == sku.id } ?: return@launch
            cartRepository.updateQuantity(item, item.quantity + 1)
            getCartItems()
        }
    }

    fun decreaseQuantity(sku: Sku) {
        viewModelScope.launch {
            val currentCart = (_cartState.value as? Result.Success)?.data ?: return@launch
            val item = currentCart.find { it.sku.id == sku.id } ?: return@launch

            if (item.quantity > 1) {
                cartRepository.updateQuantity(item, item.quantity - 1)
            } else {
                cartRepository.removeItem(item)
            }
            getCartItems()
        }
    }
}
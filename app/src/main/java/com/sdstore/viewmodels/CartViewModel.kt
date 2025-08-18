package com.sdstore.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sdstore.data.repository.CartRepository
import com.sdstore.data.repository.DeliveryRepository // DeliveryRepository کو امپورٹ کریں
import com.sdstore.models.Sku
import kotlinx.coroutines.launch

// کنسٹرکٹر میں deliveryRepository شامل کریں
class CartViewModel(
    application: Application,
    private val cartRepository: CartRepository,
    private val deliveryRepository: DeliveryRepository
) : AndroidViewModel(application) {

    private val _cartItems = MutableLiveData<List<Sku>>()
    val cartItems: LiveData<List<Sku>> = _cartItems
    val orderPlaced = MutableLiveData<Boolean>()

    init {
        refreshCart()
    }

    fun refreshCart() {
        viewModelScope.launch {
            _cartItems.postValue(cartRepository.getCart())
        }
    }

    fun addToCart(sku: Sku) {
        viewModelScope.launch {
            if (cartRepository.addToCart(sku)) { refreshCart() }
        }
    }

    fun removeFromCart(sku: Sku) {
        viewModelScope.launch {
            if (cartRepository.removeFromCart(sku)) { refreshCart() }
        }
    }

    fun placeOrder() {
        viewModelScope.launch {
            val currentItems = _cartItems.value ?: emptyList()
            if (currentItems.isNotEmpty()) {
                val success = deliveryRepository.createOrder(currentItems)
                if (success) {
                    cartRepository.clearCart()
                    refreshCart()
                    orderPlaced.postValue(true)
                } else {
                    orderPlaced.postValue(false)
                }
            }
        }
    }
}
package com.sdstore.cart.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdstore.core.data.Result
import com.sdstore.core.data.repository.CartRepository
import com.sdstore.core.models.CartItem
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
}
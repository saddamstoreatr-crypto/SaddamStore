package com.sdstore.cart.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sdstore.R
import com.sdstore.cart.data.CartRepository
import com.sdstore.core.data.Result
import com.sdstore.core.models.Sku
import com.sdstore.core.utils.Event
import com.sdstore.core.viewmodels.UiState
import com.sdstore.data.repository.DeliveryRepository
import com.sdstore.orders.data.DeliveryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class CartViewModel @Inject constructor(
    private val application: Application,
    private val cartRepository: CartRepository,
    private val deliveryRepository: DeliveryRepository
) : AndroidViewModel(application) {

    private val _cartItems = MutableStateFlow<List<Sku>>(emptyList())
    val cartItems: StateFlow<List<Sku>> = _cartItems.asStateFlow()

    private val _orderPlacementState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val orderPlacementState: StateFlow<UiState<Unit>> = _orderPlacementState.asStateFlow()

    private val _cartUpdateError = MutableStateFlow<Event<String>?>(null)
    val cartUpdateError: StateFlow<Event<String>?> = _cartUpdateError.asStateFlow()

    val totalPrice: StateFlow<Long> = _cartItems.map { items ->
        items.sumOf { it.pricePaisas * it.quantity }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = 0L
    )

    init {
        refreshCart()
    }

    fun refreshCart() {
        viewModelScope.launch {
            when (val result = cartRepository.getCart()) {
                is Result.Success -> _cartItems.value = result.data
                is Result.Error -> _cartItems.value = emptyList()
            }
        }
    }

    fun removeFromCart(sku: Sku) {
        updateItemQuantity(sku, 0)
    }

    fun updateItemQuantity(sku: Sku, newQuantity: Int) {
        val currentList = _cartItems.value.toMutableList()
        val itemIndex = currentList.indexOfFirst { it.uniqueSkuId == sku.uniqueSkuId }

        if (newQuantity > 0) {
            if (itemIndex != -1) {
                currentList[itemIndex] = currentList[itemIndex].copy(quantity = newQuantity)
            } else {
                currentList.add(sku.copy(quantity = newQuantity))
            }
        } else {
            if (itemIndex != -1) {
                currentList.removeAt(itemIndex)
            }
        }
        _cartItems.value = currentList

        viewModelScope.launch {
            when (cartRepository.updateQuantity(sku, newQuantity)) {
                is Result.Error -> {
                    _cartUpdateError.value = Event(application.getString(R.string.cart_update_failed))
                    refreshCart()
                }
                is Result.Success -> { /* Sab theek hai */ }
            }
        }
    }

    fun placeOrder() {
        viewModelScope.launch {
            val currentItems = _cartItems.value
            if (currentItems.isNotEmpty()) {
                _orderPlacementState.value = UiState.Loading
                when (val result = deliveryRepository.placeOrder(currentItems)) {
                    is Result.Success -> {
                        _orderPlacementState.value = UiState.Success(Unit)
                        _cartItems.value = emptyList()
                        cartRepository.clearCart()
                    }
                    is Result.Error -> {
                        val errorMessage = result.exception.message ?: application.getString(R.string.order_placed_failed)
                        _orderPlacementState.value = UiState.Error(errorMessage)
                    }
                }
            }
        }
    }

    fun resetOrderPlacementState() {
        _orderPlacementState.value = UiState.Idle
    }
}
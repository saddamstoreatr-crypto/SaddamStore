package com.sdstore.orders.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.sdstore.core.data.Result
import com.sdstore.core.data.repository.DeliveryRepository
import com.sdstore.core.di.AdminDeliveryRepository
import com.sdstore.core.models.Order
import com.sdstore.core.viewmodels.UiState
import com.sdstore.feature_orders.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryViewModel @Inject constructor(
    application: Application,
    @AdminDeliveryRepository private val repository: DeliveryRepository
) : AndroidViewModel(application) {

    private val _ordersState = MutableStateFlow<UiState<List<Order>>>(UiState.Loading)
    val ordersState: StateFlow<UiState<List<Order>>> = _ordersState.asStateFlow()

    private val _orderUpdateState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val orderUpdateState: StateFlow<UiState<Unit>> = _orderUpdateState.asStateFlow()

    private var lastVisibleOrder: DocumentSnapshot? = null
    private var isFetchingOrders = false
    private var allOrdersLoaded = false
    private val loadedOrders = mutableListOf<Order>()

    init {
        fetchOrders(isRefresh = true)
    }

    private fun fetchOrders(isRefresh: Boolean = false) {
        if (isFetchingOrders || (!isRefresh && allOrdersLoaded)) return
        isFetchingOrders = true

        if (isRefresh) {
            _ordersState.value = UiState.Loading
        }

        viewModelScope.launch {
            when (val result = repository.getOrders(lastVisibleOrder)) {
                is Result.Success -> {
                    val (newOrders, lastVisible) = result.data
                    lastVisibleOrder = lastVisible
                    if (isRefresh) loadedOrders.clear()
                    loadedOrders.addAll(newOrders)
                    _ordersState.value = UiState.Success(loadedOrders.toList())
                    if (newOrders.isEmpty()) {
                        allOrdersLoaded = true
                    }
                }
                is Result.Error -> {
                    _ordersState.value = UiState.Error(getApplication<Application>().getString(R.string.orders_loading_error))
                }
            }
            isFetchingOrders = false
        }
    }

    fun loadMoreOrders() {
        fetchOrders()
    }

    fun refreshOrders() {
        lastVisibleOrder = null
        isFetchingOrders = false
        allOrdersLoaded = false
        loadedOrders.clear()
        fetchOrders(isRefresh = true)
    }

    fun cancelOrder(orderId: String, reason: String) {
        _orderUpdateState.value = UiState.Loading
        viewModelScope.launch {
            when (repository.updateOrderStatus(orderId, "Cancelled", reason)) {
                is Result.Success -> {
                    _orderUpdateState.value = UiState.Success(Unit)
                    refreshOrders()
                }
                is Result.Error -> {
                    _orderUpdateState.value = UiState.Error(getApplication<Application>().getString(R.string.order_cancellation_failed))
                }
            }
        }
    }

    fun resetOrderUpdateState() {
        _orderUpdateState.value = UiState.Idle
    }
}
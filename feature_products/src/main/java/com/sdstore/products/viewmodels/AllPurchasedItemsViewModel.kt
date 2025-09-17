package com.sdstore.products.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdstore.core.R
import com.sdstore.core.data.Result
import com.sdstore.core.data.repository.DeliveryRepository // THEEK KIYA GAYA
import com.sdstore.core.di.UserDeliveryRepository
import com.sdstore.core.models.Sku
import com.sdstore.core.viewmodels.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllPurchasedItemsViewModel @Inject constructor(
    @UserDeliveryRepository private val deliveryRepository: DeliveryRepository, // THEEK KIYA GAYA
    private val application: Application
) : ViewModel() {

    private val _itemsState = MutableStateFlow<UiState<List<Sku>>>(UiState.Loading)
    val itemsState: StateFlow<UiState<List<Sku>>> = _itemsState.asStateFlow()

    init {
        fetchAllPurchasedItems()
    }

    fun fetchAllPurchasedItems() {
        _itemsState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = deliveryRepository.getAllPurchasedSkus()) {
                is Result.Success -> {
                    _itemsState.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _itemsState.value = UiState.Error(application.getString(R.string.error_loading_items))
                }
                is Result.Loading -> {
                    // This state is already handled by setting _itemsState.value = UiState.Loading before the call
                }
            }
        }
    }
}
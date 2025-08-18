package com.sdstore.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sdstore.data.repository.DeliveryRepository
import com.sdstore.models.Delivery
import kotlinx.coroutines.launch

class DeliveryViewModel(application: Application, private val repository: DeliveryRepository) : AndroidViewModel(application) {
    private val _deliveries = MutableLiveData<List<Delivery>>()
    val deliveries: LiveData<List<Delivery>> = _deliveries

    init { fetchDeliveries() }

    fun fetchDeliveries() {
        viewModelScope.launch { _deliveries.postValue(repository.getDeliveries()) }
    }
}
package com.sdstore.main.page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdstore.data.repository.ProductRepository
import com.sdstore.models.Sku
import kotlinx.coroutines.launch

class PageViewModel(private val repository: ProductRepository) : ViewModel() {
    private val _products = MutableLiveData<List<Sku>>()
    val products: LiveData<List<Sku>> = _products

    // ایرر کے لیے LiveData شامل کیا گیا
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                _products.postValue(repository.getProducts())
            } catch (e: Exception) {
                _error.postValue("پروڈکٹس لوڈ کرنے میں ناکامی: ${e.message}")
            }
        }
    }
}
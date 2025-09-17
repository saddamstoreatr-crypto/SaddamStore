package com.sdstore.products.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdstore.core.data.repository.ProductRepository
import com.sdstore.core.models.Sku
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _searchResults = MutableLiveData<List<Sku>>()
    val searchResults: LiveData<List<Sku>> = _searchResults

    fun searchProducts(query: String) {
        if (query.length < 3) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                _searchResults.value = productRepository.searchProducts(query)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
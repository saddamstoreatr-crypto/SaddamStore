package com.sdstore.products.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sdstore.core.models.Sku
import com.sdstore.products.data.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    application: Application,
    private val repository: ProductRepository
) : AndroidViewModel(application) {

    private val _searchResults = MutableStateFlow<List<Sku>>(emptyList())
    val searchResults: StateFlow<List<Sku>> = _searchResults.asStateFlow()

    fun searchProducts(query: String) {
        if (query.length < 2) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            val results = repository.searchProducts(query)
            _searchResults.value = results
        }
    }
}
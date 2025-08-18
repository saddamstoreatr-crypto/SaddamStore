package com.sdstore.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sdstore.data.repository.ProductRepository
import com.sdstore.models.Sku
import kotlinx.coroutines.launch

class SearchViewModel(application: Application, private val repository: ProductRepository) : AndroidViewModel(application) {
    private val _searchResults = MutableLiveData<List<Sku>>()
    val searchResults: LiveData<List<Sku>> = _searchResults

    fun searchProducts(query: String) {
        if (query.length < 2) {
            _searchResults.postValue(emptyList())
            return
        }
        viewModelScope.launch {
            _searchResults.postValue(repository.searchProducts(query))
        }
    }
}
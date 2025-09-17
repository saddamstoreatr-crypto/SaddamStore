package com.sdstore.products.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdstore.core.data.repository.ProductRepository
import com.sdstore.core.models.Sku
import com.sdstore.products.ui.page.HomePageItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PageViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _homePageItems = MutableLiveData<List<HomePageItem>>()
    val homePageItems: LiveData<List<HomePageItem>> = _homePageItems

    private val _products = MutableLiveData<List<Sku>>()
    val products: LiveData<List<Sku>> = _products

    init {
        loadHomePage()
    }

    private fun loadHomePage() {
        viewModelScope.launch {
            _homePageItems.value = listOf(HomePageItem.Loading)
            try {
                val banners = productRepository.getBanners()
                val categories = productRepository.getCategories()
                val regularItems = productRepository.getRegularItems()
                val items = mutableListOf<HomePageItem>()
                items.add(HomePageItem.Banners(banners))
                items.add(HomePageItem.Categories(categories))
                items.add(HomePageItem.Title("Regular Items", "regular"))
                items.add(HomePageItem.RegularItems("Your Regular Items", regularItems, "regular"))
                _homePageItems.value = items
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun getProductsByCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                _products.value = productRepository.getProductsByCategory(categoryId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
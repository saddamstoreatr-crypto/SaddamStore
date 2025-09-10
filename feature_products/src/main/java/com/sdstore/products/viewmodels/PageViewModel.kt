package com.sdstore.products.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.sdstore.R
import com.sdstore.core.data.Result
import com.sdstore.core.models.Banner
import com.sdstore.core.models.Category
import com.sdstore.core.models.OrderItem
import com.sdstore.core.models.Sku
import com.sdstore.core.viewmodels.UiState
import com.sdstore.data.repository.ProductRepository
import com.sdstore.orders.data.DeliveryRepository
import com.sdstore.products.ui.page.HomePageItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PageViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val deliveryRepository: DeliveryRepository,
    private val application: Application
) : ViewModel() {

    private val _homePageItemsState = MutableStateFlow<UiState<List<HomePageItem>>>(UiState.Loading)
    val homePageItemsState: StateFlow<UiState<List<HomePageItem>>> = _homePageItemsState.asStateFlow()

    private val _categoryProductsState = MutableStateFlow<UiState<List<Sku>>>(UiState.Loading)
    val categoryProductsState: StateFlow<UiState<List<Sku>>> = _categoryProductsState.asStateFlow()

    private var currentCategoryProducts = mutableListOf<Sku>()
    private var lastVisibleCategoryProduct: DocumentSnapshot? = null
    private var isFetchingCategoryProducts = false
    private var allCategoryProductsLoaded = false

    private var lastVisibleProduct: DocumentSnapshot? = null
    private var isFetchingProducts = false
    private var allProductsLoaded = false
    private var allProducts = mutableListOf<Sku>()

    private var searchJob: Job? = null

    private var allBanners = listOf<Banner>()
    private var allCategories = listOf<Category>()
    private var allRegularItems = listOf<Sku>()
    private var allRecentItems = listOf<OrderItem>()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        _homePageItemsState.value = UiState.Loading
        viewModelScope.launch {
            val regularItemsJob = launch { allRegularItems = productRepository.getRegularItems() }
            val bannersJob = launch { allBanners = productRepository.getBanners() }
            val categoriesJob = launch {
                val fetchedCategories = productRepository.getCategories().toMutableList()
                fetchedCategories.add(0, Category(name = "Purane Items", imageUrl = Category.PURCHASED_ITEMS_IMAGE_URL))
                allCategories = fetchedCategories
            }
            val recentItemsJob = launch {
                when(val result = deliveryRepository.getRecentlyPurchasedItems()) {
                    is Result.Success -> allRecentItems = result.data.take(10)
                    is Result.Error -> allRecentItems = emptyList()
                }
            }

            regularItemsJob.join()
            bannersJob.join()
            categoriesJob.join()
            recentItemsJob.join()

            loadMoreProducts()
        }
    }

    fun loadMoreProducts() {
        if (isFetchingProducts || allProductsLoaded) return
        isFetchingProducts = true

        viewModelScope.launch {
            try {
                val (newProducts, lastVisible) = productRepository.getProducts(lastVisibleProduct)
                allProducts.addAll(newProducts)
                lastVisibleProduct = lastVisible

                if (newProducts.isEmpty()) {
                    allProductsLoaded = true
                }
                buildHomePageList()
            } catch (e: Exception) {
                _homePageItemsState.value = UiState.Error(application.getString(R.string.product_loading_error))
            } finally {
                isFetchingProducts = false
            }
        }
    }

    private fun buildHomePageList() {
        val items = mutableListOf<HomePageItem>()
        if (allBanners.isNotEmpty()) items.add(HomePageItem.Banners(allBanners))
        if (allRecentItems.isNotEmpty()) {
            items.add(HomePageItem.Title(application.getString(R.string.buy_again_title)))
            items.add(HomePageItem.RecentItems(allRecentItems))
        }
        if (allRegularItems.isNotEmpty()) {
            items.add(HomePageItem.Title(application.getString(R.string.regular_items_title)))
            items.add(HomePageItem.RegularItems(allRegularItems))
        }
        if (allCategories.isNotEmpty()) {
            items.add(HomePageItem.Title(application.getString(R.string.all_categories_title)))
            items.add(HomePageItem.CategoriesList(allCategories))
        }
        if (allProducts.isNotEmpty()) {
            items.add(HomePageItem.Title(application.getString(R.string.all_products_title)))
            allProducts.forEach { sku -> items.add(HomePageItem.ProductItem(sku)) }
        }

        if (items.isNotEmpty()) {
            _homePageItemsState.value = UiState.Success(items)
        } else if (!isFetchingProducts) {
            _homePageItemsState.value = UiState.Success(emptyList())
        }
    }

    fun searchProducts(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            buildHomePageList()
            return
        }

        _homePageItemsState.value = UiState.Loading
        searchJob = viewModelScope.launch {
            val filteredList = productRepository.searchProducts(query)
            val searchItems = mutableListOf<HomePageItem>()

            if (filteredList.isNotEmpty()) {
                searchItems.add(HomePageItem.Title(application.getString(R.string.search_results_title)))
                filteredList.forEach { searchItems.add(HomePageItem.ProductItem(it)) }
            } else {
                searchItems.add(HomePageItem.Title(application.getString(R.string.no_results_for, query)))
            }
            _homePageItemsState.value = UiState.Success(searchItems)
        }
    }

    fun fetchProductsByCategory(categoryName: String, isInitialLoad: Boolean = false) {
        if (isFetchingCategoryProducts || (!isInitialLoad && allCategoryProductsLoaded)) return
        isFetchingCategoryProducts = true

        if (isInitialLoad) {
            currentCategoryProducts.clear()
            lastVisibleCategoryProduct = null
            allCategoryProductsLoaded = false
            _categoryProductsState.value = UiState.Loading
        }

        viewModelScope.launch {
            try {
                val (newProducts, lastVisible) = productRepository.getProductsByCategory(categoryName, lastVisibleCategoryProduct)

                if (newProducts.isEmpty() && isInitialLoad) {
                    allCategoryProductsLoaded = true
                    _categoryProductsState.value = UiState.Success(emptyList())
                } else if (newProducts.isNotEmpty()) {
                    currentCategoryProducts.addAll(newProducts)
                    _categoryProductsState.value = UiState.Success(currentCategoryProducts.toList())
                }

                lastVisibleCategoryProduct = lastVisible
                if (lastVisible == null) {
                    allCategoryProductsLoaded = true
                }
            } catch (e: Exception) {
                _categoryProductsState.value = UiState.Error(application.getString(R.string.product_loading_error))
            } finally {
                isFetchingCategoryProducts = false
            }
        }
    }
}
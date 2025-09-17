package com.sdstore.products.ui.page

import com.sdstore.core.models.Banner
import com.sdstore.core.models.Category
import com.sdstore.core.models.OrderItem
import com.sdstore.core.models.Sku

sealed class HomePageItem {
    data class Banners(val banners: List<Banner>) : HomePageItem()
    data class CategoriesList(val categories: List<Category>) : HomePageItem()
    data class ProductItem(val sku: Sku) : HomePageItem()
    data class Title(val text: String) : HomePageItem()
    data class RegularItems(val skus: List<Sku>, val string: String) : HomePageItem()
    data class RecentItems(val items: List<OrderItem>) : HomePageItem()
    object Loading : HomePageItem()
}
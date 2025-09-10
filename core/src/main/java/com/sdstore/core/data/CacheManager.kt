package com.sdstore.core.data

import com.sdstore.core.models.Banner
import com.sdstore.core.models.Category
import com.sdstore.core.models.Sku

object CacheManager {
    private const val CACHE_DURATION_MS = 15 * 60 * 1000 // 15 منٹ

    private var categoriesCache: List<Category>? = null
    private var categoriesCacheTime: Long = 0

    private var bannersCache: List<Banner>? = null
    private var bannersCacheTime: Long = 0

    private var firstPageProductsCache: List<Sku>? = null
    private var firstPageProductsCacheTime: Long = 0

    fun getCategories(): List<Category>? {
        if (categoriesCache != null && System.currentTimeMillis() - categoriesCacheTime < CACHE_DURATION_MS) {
            return categoriesCache
        }
        return null
    }

    fun setCategories(categories: List<Category>) {
        categoriesCache = categories
        categoriesCacheTime = System.currentTimeMillis()
    }

    fun getBanners(): List<Banner>? {
        if (bannersCache != null && System.currentTimeMillis() - bannersCacheTime < CACHE_DURATION_MS) {
            return bannersCache
        }
        return null
    }

    fun setBanners(banners: List<Banner>) {
        bannersCache = banners
        bannersCacheTime = System.currentTimeMillis()
    }

    fun getFirstPageProducts(): List<Sku>? {
        if (firstPageProductsCache != null && System.currentTimeMillis() - firstPageProductsCacheTime < CACHE_DURATION_MS) {
            return firstPageProductsCache
        }
        return null
    }

    fun setFirstPageProducts(products: List<Sku>) {
        firstPageProductsCache = products
        firstPageProductsCacheTime = System.currentTimeMillis()
    }
}
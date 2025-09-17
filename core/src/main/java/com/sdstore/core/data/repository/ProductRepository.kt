package com.sdstore.core.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.sdstore.core.models.Banner
import com.sdstore.core.models.Category
import com.sdstore.core.models.Sku
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProducts(page: DocumentSnapshot?): Pair<List<Sku>, DocumentSnapshot?>
    suspend fun getBanners(): Flow<List<Banner>>
    suspend fun getCategories(): Flow<List<Category>>
    suspend fun getProductById(id: String): Sku?
    suspend fun getSimilarProducts(
        productId: String,
        subCategory: String,
        category: String
    ): Flow<List<Sku>>

    suspend fun getRegularItems(): Flow<List<Sku>>
    suspend fun getProductsByCategory(categoryId: String): Flow<List<Sku>>
    suspend fun searchProducts(query: String): Flow<List<Sku>>
    suspend fun getAllPurchasedItems(): Flow<List<Sku>>
}
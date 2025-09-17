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
}
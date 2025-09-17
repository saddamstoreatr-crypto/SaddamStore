package com.sdstore.products.data

import com.google.firebase.firestore.FirebaseFirestore
import com.sdstore.core.data.repository.ProductRepository
import com.sdstore.core.models.Banner
import com.sdstore.core.models.Category
import com.sdstore.core.models.Sku
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductRepository {

    override suspend fun getBanners(): List<Banner> {
        return firestore.collection("banners").get().await().toObjects(Banner::class.java)
    }

    override suspend fun getCategories(): List<Category> {
        return firestore.collection("categories").get().await().toObjects(Category::class.java)
    }

    override suspend fun getRegularItems(): List<Sku> {
        // Implement your logic to get regular items
        return emptyList()
    }

    override suspend fun getProductsByCategory(categoryId: String): List<Sku> {
        return firestore.collection("skus").whereEqualTo("categoryId", categoryId).get().await().toObjects(Sku::class.java)
    }

    override suspend fun searchProducts(query: String): List<Sku> {
        // Implement your search logic
        return emptyList()
    }

    override suspend fun getAllPurchasedItems(): List<Sku> {
        // Implement your logic to get all purchased items
        return emptyList()
    }
}
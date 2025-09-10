package com.sdstore.products.data

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sdstore.core.data.CacheManager
import com.sdstore.core.models.Banner
import com.sdstore.core.models.Category
import com.sdstore.core.models.Sku
import com.sdstore.core.utils.Constants
import com.sdstore.core.utils.TransliterationUtils
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor() {

    private val productsCollection = Firebase.firestore.collection("products")
    private val categoriesCollection = Firebase.firestore.collection("categories")
    private val bannersCollection = Firebase.firestore.collection("banners")
    private val usersCollection = Firebase.firestore.collection("users")
    private val auth = Firebase.auth
    private val PAGE_SIZE = Constants.PRODUCT_PAGE_SIZE

    suspend fun getProducts(lastVisibleProduct: DocumentSnapshot?): Pair<List<Sku>, DocumentSnapshot?> {
        return try {
            val query = productsCollection
                .orderBy("category", Query.Direction.ASCENDING)
                .orderBy("name", Query.Direction.ASCENDING)
                .orderBy("stockQuantity", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE)
            executeQuery(query, lastVisibleProduct)
        } catch (e: Exception) {
            Log.e("FirestoreError", "Products hasil karne mein error: ", e)
            Pair(emptyList(), null)
        }
    }

    suspend fun searchProducts(query: String): List<Sku> {
        if (query.isBlank()) return emptyList()

        return try {
            val searchQuery = if (TransliterationUtils.isUrdu(query)) {
                productsCollection.whereArrayContains("searchKeywords", query)
            } else {
                productsCollection.whereArrayContains("searchKeywords", query.lowercase())
            }
            val snapshot = searchQuery.limit(50).get().await()
            snapshot.toObjects(Sku::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreError", "Products search karne mein error: ", e)
            emptyList()
        }
    }

    suspend fun getProductsByCategory(categoryName: String, lastVisibleProduct: DocumentSnapshot?): Pair<List<Sku>, DocumentSnapshot?> {
        return try {
            val query = productsCollection
                .whereEqualTo("category", categoryName)
                .orderBy("name", Query.Direction.ASCENDING)
                .limit(PAGE_SIZE)
            executeQuery(query, lastVisibleProduct)
        } catch (e: Exception) {
            Log.e("FirestoreError", "Category ke products hasil karne mein error: ", e)
            Pair(emptyList(), null)
        }
    }

    private suspend fun executeQuery(query: Query, lastVisible: DocumentSnapshot?): Pair<List<Sku>, DocumentSnapshot?> {
        val finalQuery = lastVisible?.let { query.startAfter(it) } ?: query
        val snapshot = finalQuery.get().await()
        val products = snapshot.toObjects(Sku::class.java)
        val newLastVisible = snapshot.documents.lastOrNull()
        return Pair(products, newLastVisible)
    }

    suspend fun getBanners(): List<Banner> {
        val cachedBanners = CacheManager.getBanners()
        if (cachedBanners != null) {
            return cachedBanners
        }

        return try {
            val snapshot = bannersCollection.orderBy("sortOrder", Query.Direction.ASCENDING).get().await()
            val banners = snapshot.toObjects(Banner::class.java)
            CacheManager.setBanners(banners)
            banners
        } catch (e: Exception) {
            Log.e("FirestoreError", "Banners hasil karne mein error: ", e)
            emptyList()
        }
    }

    suspend fun getCategories(): List<Category> {
        val cachedCategories = CacheManager.getCategories()
        if (cachedCategories != null) {
            return cachedCategories
        }

        return try {
            val snapshot = categoriesCollection.orderBy("sortOrder", Query.Direction.ASCENDING).get().await()
            val categories = snapshot.toObjects(Category::class.java)
            CacheManager.setCategories(categories)
            categories
        } catch (e: Exception) {
            Log.e("FirestoreError", "Categories hasil karne mein error: ", e)
            emptyList()
        }
    }

    suspend fun getRegularItems(): List<Sku> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        return try {
            val regularItemsSnapshot = usersCollection.document(userId).collection("regularItems")
                .orderBy("purchaseCount", Query.Direction.DESCENDING)
                .limit(10)
                .get().await()

            val skuIds = regularItemsSnapshot.documents.map { it.id }

            if (skuIds.isEmpty()) return emptyList()

            val productsSnapshot = productsCollection.whereIn("uniqueSkuId", skuIds).get().await()
            val skus = productsSnapshot.toObjects(Sku::class.java)

            val skuMap = skus.associateBy { it.uniqueSkuId }
            skuIds.mapNotNull { skuMap[it] }
        } catch (e: Exception) {
            Log.e("FirestoreError", "Regular items hasil karne mein error: ", e)
            emptyList()
        }
    }
}
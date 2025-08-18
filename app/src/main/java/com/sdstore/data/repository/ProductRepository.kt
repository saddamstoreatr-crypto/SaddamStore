package com.sdstore.data.repository

import com.sdstore.models.Sku
import com.sdstore.networking.ApiService

class ProductRepository(private val apiService: ApiService) {

    // --- ڈیولپمنٹ موڈ کے لیے سوئچ ---
    private val isDevelopmentMode = true

    suspend fun getProducts(): List<Sku> {
        if (isDevelopmentMode) {
            // اگر ڈیولپمنٹ موڈ ON ہے تو ڈمی ڈیٹا واپس بھیجیں
            return getDummyProducts()
        } else {
            // ورنہ، اصل API کال کریں
            return try {
                apiService.getProducts()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    // ڈمی ڈیٹا فراہم کرنے والا فنکشن
    private fun getDummyProducts(): List<Sku> {
        return listOf(
            Sku(uniqueSkuId = "sku_001", name = "کلاسک چائے", pricePaisas = 50000, unitPrice = "Rs50", imageUrl = "url", inStock = true, stickers = listOf("x10")),
            Sku(uniqueSkuId = "sku_002", name = "خالص گھی", pricePaisas = 120000, unitPrice = "Rs1200", imageUrl = "url", inStock = true, stickers = listOf("1kg")),
            Sku(uniqueSkuId = "sku_003", name = "باسمتی چاول", pricePaisas = 85000, unitPrice = "Rs850", imageUrl = "url", inStock = false, stickers = listOf("5kg"))
        )
    }

    suspend fun searchProducts(query: String): List<Sku> {
        // تلاش کے لیے بھی یہی طریقہ استعمال کیا جا سکتا ہے
        if (isDevelopmentMode) {
            return getDummyProducts().filter { it.name.contains(query, ignoreCase = true) }
        } else {
            // ... اصل API کال
            return emptyList()
        }
    }
}
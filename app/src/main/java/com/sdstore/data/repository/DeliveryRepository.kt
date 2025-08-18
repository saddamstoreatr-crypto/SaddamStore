package com.sdstore.data.repository

import com.sdstore.models.Delivery
import com.sdstore.networking.ApiService

// کنسٹرکٹر میں apiService شامل کریں
class DeliveryRepository(private val apiService: ApiService) {
    suspend fun getDeliveries(): List<Delivery> {
        return try {
            apiService.getDeliveries()
        } catch (e: Exception) {
            emptyList()
        }
    }
    // ... (createOrder کا فنکشن بھی یہاں موجود ہونا چاہیے)
}
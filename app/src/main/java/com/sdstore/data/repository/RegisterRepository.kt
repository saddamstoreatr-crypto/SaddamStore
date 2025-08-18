package com.sdstore.data.repository

import com.sdstore.networking.ApiService
import com.sdstore.networking.RegisterRequest

// کنسٹرکٹر میں apiService شامل کریں
class RegisterRepository(private val apiService: ApiService) {
    suspend fun registerUser(name: String, outletName: String, location: String): Boolean {
        return try {
            val request = RegisterRequest(name, outletName, location)
            apiService.registerUser(request).isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
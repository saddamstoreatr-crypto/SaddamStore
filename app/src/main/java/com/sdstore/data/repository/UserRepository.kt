package com.sdstore.data.repository

import com.sdstore.models.User
import com.sdstore.networking.ApiService

// کنسٹرکٹر میں apiService شامل کریں
class UserRepository(private val apiService: ApiService) {
    suspend fun getUser(): User? {
        return try {
            apiService.getUserProfile()
        } catch (e: Exception) {
            null
        }
    }
}
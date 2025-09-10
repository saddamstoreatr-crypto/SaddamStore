package com.sdstore.core.networking

import android.util.Log

class FcmRepository(private val apiService: ApiService) {

    suspend fun sendTokenToServer(token: String) {
        try {
            val request = FcmTokenRequest(token = token)
            apiService.updateFcmToken(request)
            Log.d("FcmRepository", "FCM ٹوکن کامیابی سے سرور پر بھیج دیا گیا۔")
        } catch (e: Exception) {
            Log.e("ApiError", "FCM ٹوکن بھیجنے میں ایرر: ", e)
        }
    }
}
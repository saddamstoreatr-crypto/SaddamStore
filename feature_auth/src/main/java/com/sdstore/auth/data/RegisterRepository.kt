package com.sdstore.auth.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sdstore.core.data.Result
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterRepository @Inject constructor() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    suspend fun registerUser(name: String, outletName: String, location: String, phone: String): Result<Unit> {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val exception = Exception("No user logged in. Registration failed.")
            Log.e("FirestoreError", exception.message, exception)
            return Result.Error(exception)
        }

        return try {
            val userRef = db.collection("users").document(currentUser.uid)

            val userData = mapOf(
                "name" to name,
                "outletName" to outletName,
                "location" to location,
                "phone" to phone,
                "uid" to currentUser.uid
            )

            userRef.set(userData, SetOptions.merge()).await()
            Log.d("FirestoreSuccess", "صارف کی تفصیلات کامیابی سے اپڈیٹ ہو گئیں۔")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreError", "صارف کی تفصیلات اپڈیٹ کرنے میں ایرر: ", e)
            Result.Error(e)
        }
    }
}
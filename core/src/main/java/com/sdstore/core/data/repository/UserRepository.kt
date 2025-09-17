package com.sdstore.core.data.repository // Package name tabdeel ho gaya hai

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sdstore.core.data.Result
import com.sdstore.core.models.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    // ... baqi code waisa hi rahega ...

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    suspend fun getUser(): Result<User?> {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            return Result.Error(Exception("User not logged in"))
        }
        return try {
            val document = db.collection("users").document(userId).get().await()
            Result.Success(document.toObject(User::class.java))
        } catch (e: Exception) {
            Log.e("FirestoreError", "User ka profile hasil karne mein error: ", e)
            Result.Error(e)
        }
    }

    suspend fun updateUserProfile(name: String, outletName: String): Result<Unit> {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            return Result.Error(Exception("User not logged in"))
        }
        return try {
            val userUpdates = mapOf(
                "name" to name,
                "outletName" to outletName
            )
            db.collection("users").document(userId).update(userUpdates).await()
            Log.d("FirestoreSuccess", "Profile kamyabi se update ho gayi.")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreError", "Profile update karne mein error: ", e)
            Result.Error(e)
        }
    }
}
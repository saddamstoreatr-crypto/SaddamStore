package com.sdstore.orders.data

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sdstore.core.data.Result
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackRepository @Inject constructor() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    suspend fun submitFeedback(feedbackText: String): Result<Unit> {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            return Result.Error(Exception("User not logged in"))
        }

        return try {
            val feedbackData = hashMapOf(
                "feedbackText" to feedbackText,
                "userId" to currentUser.uid,
                "userContact" to (currentUser.email ?: currentUser.phoneNumber ?: "N/A"),
                "timestamp" to FieldValue.serverTimestamp()
            )

            db.collection("feedback").add(feedbackData).await()
            Log.d("FirestoreSuccess", "Feedback kamyabi se submit ho gaya.")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreError", "Feedback submit karne mein error: ", e)
            Result.Error(e)
        }
    }
}
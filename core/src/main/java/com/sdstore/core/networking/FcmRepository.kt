package com.sdstore.core.networking

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface FcmRepository {
    suspend fun sendFcmToken(token: String)
}

@Singleton
class FcmRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FcmRepository {

    override suspend fun sendFcmToken(token: String) {
        auth.currentUser?.let { user ->
            firestore.collection("users").document(user.uid).update(
                mapOf(
                    "fcmToken" to token
                )
            ).await()
        }
    }
}
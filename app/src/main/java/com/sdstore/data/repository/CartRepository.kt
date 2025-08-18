package com.sdstore.data.repository

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sdstore.models.Sku
import com.sdstore.networking.ApiService
import kotlinx.coroutines.tasks.await

class CartRepository(private val apiService: ApiService) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private fun getUserCartCollection() = auth.currentUser?.uid?.let { userId ->
        db.collection("users").document(userId).collection("cart")
    }

    suspend fun getCart(): List<Sku> {
        val cartCollection = getUserCartCollection() ?: return emptyList()
        return try {
            val snapshot = cartCollection.get().await()
            snapshot.toObjects(Sku::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreError", "کارٹ حاصل کرنے میں ایرر: ", e)
            emptyList()
        }
    }

    suspend fun addToCart(product: Sku): Boolean {
        // ... (پچھلا کوڈ)
        return true
    }

    suspend fun removeFromCart(product: Sku): Boolean {
        // ... (پچھلا کوڈ)
        return true
    }

    // یہ گمشدہ فنکشن شامل کریں
    suspend fun clearCart(): Boolean {
        val cartCollection = getUserCartCollection() ?: return false
        return try {
            val snapshot = cartCollection.get().await()
            for (document in snapshot.documents) {
                document.reference.delete().await()
            }
            true
        } catch(e: Exception) {
            Log.e("FirestoreError", "کارٹ صاف کرنے میں ایرر: ", e)
            false
        }
    }
}
package com.sdstore.cart.data

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sdstore.core.data.Result
import com.sdstore.core.models.Sku
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private fun getUserCartCollection() = auth.currentUser?.uid?.let { userId ->
        db.collection("users").document(userId).collection("cart")
    }

    suspend fun getCart(): Result<List<Sku>> {
        val cartCollection = getUserCartCollection()
            ?: return Result.Error(Exception("User not logged in"))
        return try {
            val snapshot = cartCollection.get().await()
            Result.Success(snapshot.toObjects(Sku::class.java))
        } catch (e: Exception) {
            Log.e("FirestoreError", "کارٹ حاصل کرنے میں ایرر: ", e)
            Result.Error(e)
        }
    }

    suspend fun updateQuantity(product: Sku, newQuantity: Int): Result<Unit> {
        val cartCollection = getUserCartCollection()
            ?: return Result.Error(Exception("User not logged in"))
        return try {
            val docRef = cartCollection.document(product.uniqueSkuId)
            if (newQuantity > 0) {
                val cartItem = product.copy(quantity = newQuantity)
                docRef.set(cartItem).await()
            } else {
                docRef.delete().await()
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreError", "تعداد اپ ڈیٹ کرنے میں ایرر: ", e)
            Result.Error(e)
        }
    }

    suspend fun clearCart(): Result<Unit> {
        val cartCollection = getUserCartCollection()
            ?: return Result.Error(Exception("User not logged in"))
        return try {
            val snapshot = cartCollection.get().await()
            for (document in snapshot.documents) {
                document.reference.delete().await()
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreError", "کارٹ صاف کرنے میں ایرر: ", e)
            Result.Error(e)
        }
    }
}
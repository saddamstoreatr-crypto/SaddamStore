package com.sdstore.cart.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sdstore.core.data.Result
import com.sdstore.core.data.repository.CartRepository
import com.sdstore.core.models.CartItem
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : CartRepository {

    private val currentUser get() = auth.currentUser

    private fun getCartCollection() = firestore.collection("users")
        .document(currentUser!!.uid)
        .collection("cart")

    override suspend fun getCartItems(): Result<List<CartItem>> {
        if (currentUser == null) return Result.Error(Exception("User not logged in."))
        return try {
            val snapshot = getCartCollection().get().await()
            val cartItems = snapshot.toObjects(CartItem::class.java)
            Result.Success(cartItems)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateQuantity(item: CartItem, quantity: Int): Result<Unit> {
        if (currentUser == null) return Result.Error(Exception("User not logged in."))
        return try {
            // Fix: Sku.kt theek hone ke baad ab 'item.sku.id' ka error نہیں aayega.
            val documentId = item.sku.id
            getCartCollection().document(documentId).update("quantity", quantity).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun removeItem(item: CartItem): Result<Unit> {
        if (currentUser == null) return Result.Error(Exception("User not logged in."))
        return try {
            // Fix: Sku.kt theek hone ke baad ab 'item.sku.id' ka error نہیں aayega.
            val documentId = item.sku.id
            getCartCollection().document(documentId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun placeOrder(): Result<Unit> {
        if (currentUser == null) return Result.Error(Exception("Order place karne ke liye login zaroori hai."))
        return Result.Success(Unit) // Placeholder
    }

    override suspend fun addItem(item: CartItem): Result<Unit> {
        if (currentUser == null) return Result.Error(Exception("User not logged in."))
        return try {
            getCartCollection().document(item.sku.id).set(item).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
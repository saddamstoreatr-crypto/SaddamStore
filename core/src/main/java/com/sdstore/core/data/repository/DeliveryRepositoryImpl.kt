package com.sdstore.core.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sdstore.core.data.Result
import com.sdstore.core.models.CartItem
import com.sdstore.core.models.Delivery
import com.sdstore.core.models.Order
import com.sdstore.core.models.OrderItem
import com.sdstore.core.networking.ApiService
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeliveryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val apiService: ApiService
) : DeliveryRepository {

    private val currentUser get() = auth.currentUser

    override suspend fun getDeliveries(): Result<List<Delivery>> {
        if (currentUser == null) return Result.Error(Exception("User not logged in"))

        return try {
            val snapshot = firestore.collection("users")
                .document(currentUser!!.uid)
                .collection("deliveries")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
            val deliveries = snapshot.toObjects(Delivery::class.java)
            Result.Success(deliveries)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun placeOrderFromCart(): Result<String> {
        if (currentUser == null) return Result.Error(Exception("User not logged in"))

        return try {
            val cartSnapshot = firestore.collection("users")
                .document(currentUser!!.uid)
                .collection("cart")
                .get()
                .await()

            val cartItems = cartSnapshot.toObjects(CartItem::class.java)

            // Stock check karne ke liye loop
            for (item in cartItems) {
                val skuDoc = firestore.collection("skus").document(item.sku.id).get().await()
                val stock = skuDoc.getLong("stock")?.toInt() ?: 0
                if (stock < item.quantity) {
                    return Result.Error(Exception("Stock for ${item.sku.name} is not enough."))
                }
            }

            val orderItems = cartItems.map { cartItem ->
                OrderItem(
                    uniqueSkuId = cartItem.sku.id,
                    name = cartItem.sku.name,
                    imageUrl = cartItem.sku.imageUrl ?: "",
                    quantity = cartItem.quantity.toLong(),
                    pricePaisas = cartItem.sku.price.toLong()
                )
            }

            val totalPrice = orderItems.sumOf { it.pricePaisas * it.quantity }

            // Order banayein
            val order = Order(
                userId = currentUser!!.uid,
                items = orderItems,
                totalPrice = totalPrice,
            )

            val orderRef = firestore.collection("orders").add(order).await()

            // Cart khali karein
            for (item in cartItems) {
                firestore.collection("users")
                    .document(currentUser!!.uid)
                    .collection("cart")
                    .document(item.sku.id)
                    .delete()
                    .await()
            }

            Result.Success(orderRef.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun cancelDelivery(deliveryId: String): Result<Unit> {
        if (currentUser == null) return Result.Error(Exception("User not logged in"))
        return try {
            firestore.collection("deliveries").document(deliveryId).update("status", "cancelled").await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getOrders(lastVisible: DocumentSnapshot?): Result<Pair<List<Order>, DocumentSnapshot?>> {
        throw UnsupportedOperationException("This operation is not supported for user-facing repository.")
    }

    override suspend fun updateOrderStatus(orderId: String, status: String, reason: String?): Result<Unit> {
        throw UnsupportedOperationException("This operation is not supported for user-facing repository.")
    }

    override suspend fun getAllPurchasedSkus(): Result<List<com.sdstore.core.models.Sku>> {
        if (currentUser == null) return Result.Error(Exception("User not logged in"))

        return try {
            // 1. Fetch all orders for the current user
            val ordersSnapshot = firestore.collection("orders")
                .whereEqualTo("userId", currentUser!!.uid)
                .get()
                .await()
            val orders = ordersSnapshot.toObjects(Order::class.java)

            // 2. Get all unique SKU IDs from the orders
            val skuIds = orders.flatMap { it.items }.map { it.uniqueSkuId }.toSet()

            if (skuIds.isEmpty()) {
                return Result.Success(emptyList())
            }

            // 3. Fetch SKUs in chunks to handle Firestore's 'whereIn' limit of 30
            val allSkus = mutableListOf<com.sdstore.core.models.Sku>()
            skuIds.chunked(30).forEach { chunk ->
                val skusSnapshot = firestore.collection("skus")
                    .whereIn(com.google.firebase.firestore.FieldPath.documentId(), chunk)
                    .get()
                    .await()
                allSkus.addAll(skusSnapshot.toObjects(com.sdstore.core.models.Sku::class.java))
            }

            Result.Success(allSkus)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

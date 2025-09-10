package com.sdstore.orders.data

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sdstore.core.data.Result
import com.sdstore.core.models.Order
import com.sdstore.core.models.OrderItem
import com.sdstore.core.models.Sku
import com.sdstore.core.utils.Constants
import kotlinx.coroutines.tasks.await
import java.text.DecimalFormat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeliveryRepository @Inject constructor() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val productsCollection = db.collection("products")
    private val PAGE_SIZE = Constants.ORDER_PAGE_SIZE

    suspend fun placeOrder(items: List<Sku>): Result<Unit> {
        val userId = auth.currentUser?.uid
            ?: return Result.Error(Exception("User not logged in"))

        if (items.isEmpty()) {
            return Result.Error(Exception("Cart is empty."))
        }

        return try {
            db.runTransaction { transaction ->
                val counterRef = db.collection("counters").document("order_counter")
                val counterSnapshot = transaction.get(counterRef)
                val newOrderIdNumber = (counterSnapshot.getLong("current_number") ?: 0) + 1
                val formattedOrderId = DecimalFormat("000000").format(newOrderIdNumber)

                val productRefsAndData = items.map { item ->
                    val productRef = productsCollection.document(item.uniqueSkuId)
                    val productSnapshot = transaction.get(productRef)
                    Triple(productRef, productSnapshot, item)
                }

                val validatedItems = mutableListOf<OrderItem>()
                var validatedTotalPrice: Long = 0

                for ((_, productSnapshot, item) in productRefsAndData) {
                    val productFromDb = productSnapshot.toObject(Sku::class.java)
                    if (productFromDb != null) {
                        if (productFromDb.stockQuantity < item.quantity) {
                            throw FirebaseFirestoreException(
                                "Item '${item.name}' ka stock khatam ho gaya hai.",
                                FirebaseFirestoreException.Code.ABORTED
                            )
                        }
                        validatedItems.add(
                            OrderItem(
                                uniqueSkuId = item.uniqueSkuId,
                                name = item.name,
                                pricePaisas = productFromDb.pricePaisas,
                                imageUrl = item.imageUrl,
                                quantity = item.quantity
                            )
                        )
                        validatedTotalPrice += productFromDb.pricePaisas * item.quantity
                    } else {
                        throw FirebaseFirestoreException("Product ${item.name} nahi mila.", FirebaseFirestoreException.Code.ABORTED)
                    }
                }

                if (validatedItems.isEmpty()) {
                    throw FirebaseFirestoreException("Order mein koi aisi item nahi jo bheji ja sake.", FirebaseFirestoreException.Code.ABORTED)
                }

                for ((productRef, productSnapshot, item) in productRefsAndData) {
                    val currentStock = productSnapshot.getLong("stockQuantity") ?: 0
                    val newStock = currentStock - item.quantity
                    transaction.update(productRef, "stockQuantity", newStock)
                }

                val newOrderData = hashMapOf(
                    "orderId" to formattedOrderId,
                    "userId" to userId,
                    "items" to validatedItems,
                    "totalPrice" to validatedTotalPrice,
                    "status" to "Pending",
                    "createdAt" to FieldValue.serverTimestamp()
                )
                val orderRef = db.collection("orders").document(formattedOrderId)
                transaction.set(orderRef, newOrderData)

                transaction.set(counterRef, mapOf("current_number" to newOrderIdNumber))

            }.await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreError", "Order place karne mein error: ", e)
            Result.Error(e)
        }
    }

    suspend fun getOrders(lastVisibleOrder: DocumentSnapshot?): Result<Pair<List<Order>, DocumentSnapshot?>> {
        val userId = auth.currentUser?.uid
            ?: return Result.Error(Exception("User not logged in"))
        return try {
            val query = db.collection("orders")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE)

            val finalQuery = lastVisibleOrder?.let { query.startAfter(it) } ?: query

            val snapshot = finalQuery.get().await()
            val orders = snapshot.toObjects(Order::class.java)
            val lastVisible = snapshot.documents.lastOrNull()

            Result.Success(Pair(orders, lastVisible))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String, reason: String? = null): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "status" to newStatus
            )
            if (reason != null) {
                updates["cancellationReason"] = reason
            }
            db.collection("orders").document(orderId).update(updates).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getAllPurchasedItems(): Result<List<Sku>> {
        val userId = auth.currentUser?.uid ?: return Result.Error(Exception("User not logged in"))
        return try {
            val query = db.collection("orders")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)

            val snapshot = query.get().await()
            val orders = snapshot.toObjects(Order::class.java)

            val uniqueSkuIds = orders
                .flatMap { it.items }
                .map { it.uniqueSkuId }
                .distinct()

            if (uniqueSkuIds.isEmpty()) {
                return Result.Success(emptyList())
            }

            val products = mutableListOf<Sku>()
            for (chunk in uniqueSkuIds.chunked(10)) {
                val productsSnapshot = productsCollection
                    .whereIn("uniqueSkuId", chunk)
                    .get()
                    .await()
                products.addAll(productsSnapshot.toObjects(Sku::class.java))
            }

            Result.Success(products)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getRecentlyPurchasedItems(): Result<List<OrderItem>> {
        val userId = auth.currentUser?.uid ?: return Result.Error(Exception("User not logged in"))
        return try {
            val query = db.collection("orders")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(10)

            val snapshot = query.get().await()
            val orders = snapshot.toObjects(Order::class.java)

            val uniqueItems = linkedMapOf<String, OrderItem>()
            orders.flatMap { it.items }.forEach { item ->
                if (!uniqueItems.containsKey(item.uniqueSkuId)) {
                    uniqueItems[item.uniqueSkuId] = item
                }
            }

            Result.Success(uniqueItems.values.toList())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
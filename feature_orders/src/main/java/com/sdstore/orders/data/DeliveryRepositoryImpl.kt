package com.sdstore.orders.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sdstore.core.data.Result
import com.sdstore.core.data.repository.DeliveryRepository
import com.sdstore.core.models.Order
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DeliveryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : DeliveryRepository {

    override suspend fun getOrders(lastVisible: DocumentSnapshot?): Result<Pair<List<Order>, DocumentSnapshot?>> {
        return try {
            val query = firestore.collection("orders")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(10)

            val finalQuery = lastVisible?.let { query.startAfter(it) } ?: query

            val snapshot = finalQuery.get().await()
            val orders = snapshot.toObjects(Order::class.java)
            val newLastVisible = snapshot.documents.lastOrNull()

            Result.Success(Pair(orders, newLastVisible))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateOrderStatus(orderId: String, status: String, reason: String?): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "status" to status
            )
            if (reason != null) {
                updates["cancellationReason"] = reason
            }
            firestore.collection("orders").document(orderId).update(updates).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
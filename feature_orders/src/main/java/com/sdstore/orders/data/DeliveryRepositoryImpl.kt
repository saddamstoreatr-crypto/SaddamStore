package com.sdstore.orders.data

import com.google.firebase.firestore.DocumentSnapshot
import com.sdstore.core.data.Result
import com.sdstore.core.data.repository.DeliveryRepository
import com.sdstore.core.models.Delivery
import com.sdstore.core.models.Order
import com.sdstore.core.models.Sku
import javax.inject.Inject

class DeliveryRepositoryImpl @Inject constructor() : DeliveryRepository {

    override suspend fun getAllPurchasedSkus(): Result<List<Sku>> {
        // This is a stub implementation.
        return Result.Success(emptyList())
    }

    override suspend fun getDeliveries(): Result<List<Delivery>> {
        throw UnsupportedOperationException("This operation is not supported for user-facing repository.")
    }

    override suspend fun placeOrderFromCart(): Result<String> {
        throw UnsupportedOperationException("This operation is not supported for user-facing repository.")
    }

    override suspend fun cancelDelivery(deliveryId: String): Result<Unit> {
        throw UnsupportedOperationException("This operation is not supported for user-facing repository.")
    }

    override suspend fun getOrders(lastVisible: DocumentSnapshot?): Result<Pair<List<Order>, DocumentSnapshot?>> {
        throw UnsupportedOperationException("This operation is not supported for user-facing repository.")
    }

    override suspend fun updateOrderStatus(orderId: String, status: String, reason: String?): Result<Unit> {
        throw UnsupportedOperationException("This operation is not supported for user-facing repository.")
    }
}

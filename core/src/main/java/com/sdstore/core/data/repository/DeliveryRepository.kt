package com.sdstore.core.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.sdstore.core.data.Result
import com.sdstore.core.models.Delivery
import com.sdstore.core.models.Order
import com.sdstore.core.models.Sku

interface DeliveryRepository {

    suspend fun getAllPurchasedSkus(): Result<List<Sku>>

    suspend fun getDeliveries(): Result<List<Delivery>>

    suspend fun placeOrderFromCart(): Result<String>

    suspend fun cancelDelivery(deliveryId: String): Result<Unit>

    suspend fun getOrders(lastVisible: DocumentSnapshot?): Result<Pair<List<Order>, DocumentSnapshot?>>

    suspend fun updateOrderStatus(orderId: String, status: String, reason: String?): Result<Unit>
}
package com.sdstore.core.models

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Order(
    val orderId: String = "",
    val userId: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalPrice: Long = 0,
    @ServerTimestamp
    val createdAt: Date? = null,
    val status: String = "Pending",
    val cancellationReason: String? = null
) : Parcelable
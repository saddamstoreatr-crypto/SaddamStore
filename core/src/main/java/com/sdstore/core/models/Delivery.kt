package com.sdstore.core.models

import com.google.gson.annotations.SerializedName

data class Delivery(
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("date")
    val date: String, // e.g., "منگل، 19 اگست"
    @SerializedName("status")
    val status: String, // e.g., "شیڈیول شدہ", "مکمل", "ناکام"
    @SerializedName("total_amount")
    val totalAmount: Double
)
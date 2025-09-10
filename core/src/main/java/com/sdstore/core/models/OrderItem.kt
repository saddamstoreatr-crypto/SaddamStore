package com.sdstore.core.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItem(
    val uniqueSkuId: String = "",
    val name: String = "",
    val pricePaisas: Long = 0,
    val imageUrl: String = "",
    val quantity: Int = 1
) : Parcelable
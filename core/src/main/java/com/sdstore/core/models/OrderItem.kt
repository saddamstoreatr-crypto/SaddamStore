package com.sdstore.core.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItem(
    val uniqueSkuId: String = "",
    val name: String = "",
    val pricePaisas: Long = 0,
    val imageUrl: String = "",
    // --- TABDEELI: quantity ko Int se Long kar diya gaya hai ---
    val quantity: Long = 1,
    // --- TABDEELI: unitInfo ki field shamil ki gayi hai ---
    val unitInfo: String = ""
) : Parcelable
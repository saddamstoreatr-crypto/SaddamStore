package com.sdstore.models

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties // Firestore کے لیے اہم ہے تاکہ اضافی فیلڈز کو نظر انداز کیا جا سکے
data class Sku(
    val uniqueSkuId: String = "",
    val name: String = "",
    val pricePaisas: Long = 0,
    val unitPrice: String = "", // نئی فیلڈ: جیسے "Rs30"
    val imageUrl: String = "",
    val inStock: Boolean = true, // نئی فیلڈ: اسٹاک کی حالت کے لیے
    val stickers: List<String> = emptyList() // نئی فیلڈ: جیسے ["x10", "14g"]
) : Parcelable
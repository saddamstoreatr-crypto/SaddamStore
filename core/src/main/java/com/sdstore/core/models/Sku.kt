package com.sdstore.core.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "products")
@IgnoreExtraProperties
data class Sku(
    @PrimaryKey val uniqueSkuId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val pricePaisas: Long = 0,
    val category: String = "",
    val stockQuantity: Long = 0,
    val unitInfo: String = "",
    var quantity: Int = 1,
    val searchKeywords: List<String> = emptyList()
) : Parcelable
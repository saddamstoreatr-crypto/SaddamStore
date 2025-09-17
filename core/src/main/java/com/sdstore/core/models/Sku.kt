package com.sdstore.core.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

/**
 * Yeh data class ek product (SKU) ko represent karti hai.
 * Isay Firestore se data read/write karne aur Room database mein save karne ke liye istemal kiya jata hai.
 *
 * @param id Har product ka unique identifier.
 */
// Fix: @Entity annotation add ki gayi hai. Is se Room ko pata chalega ke 'products' naam ka table banana hai.
@Parcelize
@Entity(tableName = "products")
@IgnoreExtraProperties
data class Sku(
    // Fix: @PrimaryKey annotation add ki gayi hai. Yeh 'id' ko table ka unique identifier banati hai.
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val price: Int = 0,
    val imageUrl: String? = null,
    val categoryId: String = "",
    val stock: Int = 0,
    val description: String = "",
    val unitInfo: String = ""
) : Parcelable
package com.sdstore.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class Discount(
    @SerializedName("id")
    val id: String,
    @SerializedName("label")
    val label: String,
    @SerializedName("slabs")
    val discounts: List<DiscountSlab>
) : Parcelable {
    fun currentDiscountSlab(quantity: Long): DiscountSlab {
        return discounts.last { quantity >= it.quantity }
    }
}
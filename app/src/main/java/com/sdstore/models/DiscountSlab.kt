package com.sdstore.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class DiscountSlab(
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("price_paisas")
    val pricePaisas: Long
) : Parcelable
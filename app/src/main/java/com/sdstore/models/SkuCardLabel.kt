package com.sdstore.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class SkuCardLabel(
    @SerializedName("id")
    val id: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("font_color")
    val fontColor: String,
    @SerializedName("bg_color")
    val bgColor: String
) : Parcelable
package com.sdstore.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("store_name")
    val storeName: String,
    @SerializedName("auth_token")
    val authToken: String
)
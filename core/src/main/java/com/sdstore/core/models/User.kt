package com.sdstore.core.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val outletName: String = "",
    val location: String = "",
    @ServerTimestamp
    val createdAt: Date? = null,
    val imageUrl: String = ""
)
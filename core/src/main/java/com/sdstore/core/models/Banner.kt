package com.sdstore.core.models

data class Banner(
    val imageUrl: String = "",
    val targetUrl: String? = null,
    val sortOrder: Int = 0
)
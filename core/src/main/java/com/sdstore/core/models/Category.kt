package com.sdstore.core.models

import com.google.firebase.firestore.PropertyName

data class Category(
    val name: String = "",
    @get:PropertyName("imageURL") @set:PropertyName("imageURL")
    var imageUrl: String = "",
    val sortOrder: Int = 0
) {
    companion object {
        const val PURCHASED_ITEMS_IMAGE_URL = "filter_icon"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Category
        if (name != other.name) return false
        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
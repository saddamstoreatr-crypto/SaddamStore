package com.sdstore.core.models

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Yeh data class cart ke ek single item ko represent karti hai.
 * @param sku Is item ki product details.
 * @param quantity Is item ki taadad (quantity).
 */
@IgnoreExtraProperties
data class CartItem(
    val sku: Sku = Sku(),
    val quantity: Int = 0
)
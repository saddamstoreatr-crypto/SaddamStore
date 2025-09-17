package com.sdstore.core.data.repository

import com.sdstore.core.data.Result
import com.sdstore.core.models.CartItem // Fix: CartItem ko yahan import kiya gaya hai.

/**
 * Cart se mutalliq tamam data operations ke liye yeh interface hai.
 */
interface CartRepository {

    /**
     * User ke cart mein mojood tamam items hasil karta hai.
     */
    suspend fun getCartItems(): Result<List<CartItem>>

    /**
     * Cart mein mojood items ka order place karta hai.
     */
    suspend fun placeOrder(): Result<Unit>

    /**
     * Cart mein kisi item ki quantity (taadad) update karta hai.
     */
    suspend fun updateQuantity(item: CartItem, quantity: Int): Result<Unit>

    /**
     * Cart se ek item remove (delete) karta hai.
     */
    suspend fun removeItem(item: CartItem): Result<Unit>
}
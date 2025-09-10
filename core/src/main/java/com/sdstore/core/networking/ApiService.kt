package com.sdstore.core.networking

import com.sdstore.core.models.Delivery
import com.sdstore.core.models.Sku
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class CartActionRequest(val skuId: String, val quantity: Int)
data class CreateOrderRequest(val items: List<Sku>)
data class FcmTokenRequest(val token: String)

interface ApiService {
    @GET("products/list")
    suspend fun getProducts(): List<Sku>

    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): List<Sku>

    @GET("cart")
    suspend fun getCartItems(): List<Sku>

    @POST("cart/add")
    suspend fun addToCart(@Body request: CartActionRequest): Response<Unit>

    @POST("cart/remove")
    suspend fun removeFromCart(@Body request: CartActionRequest): Response<Unit>

    @GET("deliveries/list")
    suspend fun getDeliveries(): List<Delivery>

    @POST("orders/create")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<Unit>

    @POST("user/fcm-token")
    suspend fun updateFcmToken(@Body request: FcmTokenRequest): Response<Unit>
}
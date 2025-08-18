package com.sdstore.networking

import com.sdstore.models.Delivery
import com.sdstore.models.Sku
import com.sdstore.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class RegisterRequest(val name: String, val outletName: String, val location: String)
data class CartActionRequest(val skuId: String, val quantity: Int)

interface ApiService {
    @GET("products/list")
    suspend fun getProducts(): List<Sku>

    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): List<Sku>

    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<Unit>

    @GET("user/profile")
    suspend fun getUserProfile(): User

    @GET("cart")
    suspend fun getCartItems(): List<Sku>

    @POST("cart/add")
    suspend fun addToCart(@Body request: CartActionRequest): Response<Unit>

    @POST("cart/remove")
    suspend fun removeFromCart(@Body request: CartActionRequest): Response<Unit>

    @GET("deliveries/list")
    suspend fun getDeliveries(): List<Delivery>
}
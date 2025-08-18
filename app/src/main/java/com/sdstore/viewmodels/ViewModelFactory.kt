package com.sdstore.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sdstore.data.repository.*
import com.sdstore.main.page.PageViewModel
import com.sdstore.networking.RetrofitClient

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val apiService = RetrofitClient.instance

        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(application) as T

            modelClass.isAssignableFrom(CartViewModel::class.java) ->
                CartViewModel(application, CartRepository(apiService), DeliveryRepository(apiService)) as T

            modelClass.isAssignableFrom(PageViewModel::class.java) -> PageViewModel(ProductRepository(apiService)) as T
            modelClass.isAssignableFrom(UserViewModel::class.java) -> UserViewModel(application, UserRepository(apiService)) as T
            modelClass.isAssignableFrom(DeliveryViewModel::class.java) -> DeliveryViewModel(application, DeliveryRepository(apiService)) as T
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel(application, ProductRepository(apiService)) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(application, RegisterRepository(apiService)) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
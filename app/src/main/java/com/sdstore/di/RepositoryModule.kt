// File: app/src/main/java/com/sdstore/di/RepositoryModule.kt

package com.sdstore.di // <-- YEH TABDEELI SAB SE AHEM HAI

import com.sdstore.cart.data.CartRepository
import com.sdstore.orders.data.DeliveryRepository
import com.sdstore.orders.data.FeedbackRepository
import com.sdstore.products.data.ProductRepository
import com.sdstore.auth.data.RegisterRepository
import com.sdstore.core.data.repository.UserRepository
import com.sdstore.core.networking.ApiService
import com.sdstore.core.networking.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProductRepository(): ProductRepository {
        return ProductRepository()
    }

    @Provides
    @Singleton
    fun provideCartRepository(): CartRepository {
        return CartRepository()
    }

    @Provides
    @Singleton
    fun provideDeliveryRepository(): DeliveryRepository {
        return DeliveryRepository()
    }

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepository()
    }

    @Provides
    @Singleton
    fun provideRegisterRepository(): RegisterRepository {
        return RegisterRepository()
    }

    @Provides
    @Singleton
    fun provideFeedbackRepository(): FeedbackRepository {
        return FeedbackRepository()
    }

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitClient.instance
    }
}
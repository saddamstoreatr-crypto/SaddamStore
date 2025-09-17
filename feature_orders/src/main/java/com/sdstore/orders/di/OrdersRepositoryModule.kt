package com.sdstore.products.di

import com.sdstore.core.data.repository.DeliveryRepository
import com.sdstore.core.di.UserDeliveryRepository
import com.sdstore.products.data.DeliveryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductsRepositoryModule {

    @Binds
    @Singleton
    @UserDeliveryRepository
    abstract fun bindDeliveryRepository(impl: DeliveryRepositoryImpl): DeliveryRepository
}

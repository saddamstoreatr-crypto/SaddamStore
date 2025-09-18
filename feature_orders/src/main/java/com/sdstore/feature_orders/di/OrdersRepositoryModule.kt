package com.sdstore.feature_orders.di

import com.sdstore.core.data.repository.DeliveryRepository
import com.sdstore.core.di.AdminDeliveryRepository
import com.sdstore.feature_orders.data.DeliveryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OrdersRepositoryModule {

    @Binds
    @Singleton
    @AdminDeliveryRepository
    abstract fun bindDeliveryRepository(impl: DeliveryRepositoryImpl): DeliveryRepository
}

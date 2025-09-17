package com.sdstore.orders.di

import com.google.firebase.firestore.FirebaseFirestore
import com.sdstore.core.data.repository.DeliveryRepository
import com.sdstore.orders.data.DeliveryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OrdersModule {

    @Provides
    @Singleton
    fun provideDeliveryRepository(firestore: FirebaseFirestore): DeliveryRepository {
        return DeliveryRepositoryImpl(firestore)
    }
}
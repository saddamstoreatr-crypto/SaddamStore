package com.sdstore.products.di

import com.google.firebase.firestore.FirebaseFirestore
import com.sdstore.core.data.repository.ProductRepository
import com.sdstore.products.data.ProductRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductsModule {

    @Provides
    @Singleton
    fun provideProductRepository(firestore: FirebaseFirestore): ProductRepository {
        return ProductRepositoryImpl(firestore)
    }
}
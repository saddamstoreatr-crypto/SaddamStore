package com.sdstore.cart.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sdstore.cart.data.CartRepositoryImpl
import com.sdstore.core.data.repository.CartRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CartModule {

    @Provides
    @Singleton
    fun provideCartRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): CartRepository {
        // Hilt ko batata hai ke jab bhi CartRepository ki zaroorat ho,
        // to CartRepositoryImpl ka instance provide karo.
        return CartRepositoryImpl(firestore, auth)
    }
}
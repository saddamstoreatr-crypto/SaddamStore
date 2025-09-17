package com.sdstore.auth.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sdstore.auth.data.RegisterRepositoryImpl
import com.sdstore.core.data.repository.RegisterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideRegisterRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): RegisterRepository {
        return RegisterRepositoryImpl(auth, firestore)
    }
}
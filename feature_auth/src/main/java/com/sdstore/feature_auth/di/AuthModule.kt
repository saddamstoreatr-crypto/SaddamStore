package com.sdstore.feature_auth.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sdstore.core.data.repository.RegisterRepository
import com.sdstore.feature_auth.data.RegisterRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object AuthModule {

    @Provides
    @ActivityScoped
    fun provideRegisterRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): RegisterRepository {
        return RegisterRepositoryImpl(auth, firestore, storage)
    }
}
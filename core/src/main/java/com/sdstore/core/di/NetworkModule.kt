package com.sdstore.core.di

import com.sdstore.core.networking.ApiService
import com.sdstore.core.networking.FcmRepository
import com.sdstore.core.networking.FcmRepositoryImpl
import com.sdstore.core.networking.RetrofitClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindFcmRepository(
        fcmRepositoryImpl: FcmRepositoryImpl
    ): FcmRepository

    companion object {
        @Provides
        @Singleton
        fun provideApiService(): ApiService {
            return RetrofitClient.instance
        }
    }
}
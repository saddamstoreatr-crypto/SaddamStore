package com.sdstore.core.di

import com.sdstore.core.data.repository.UserRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AuthModuleDependencies {
    fun userRepository(): UserRepository
}

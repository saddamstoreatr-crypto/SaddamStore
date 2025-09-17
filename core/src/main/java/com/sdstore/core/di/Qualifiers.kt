package com.sdstore.core.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserDeliveryRepository

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AdminDeliveryRepository

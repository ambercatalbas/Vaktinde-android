package com.ambercatalbas.vaktinde.core.data.di

import com.ambercatalbas.vaktinde.core.data.repository.NotificationRepositoryImpl
import com.ambercatalbas.vaktinde.core.data.repository.PrayerTimeRepositoryImpl
import com.ambercatalbas.vaktinde.core.data.repository.UserPreferencesRepositoryImpl
import com.ambercatalbas.vaktinde.core.domain.repository.NotificationRepository
import com.ambercatalbas.vaktinde.core.domain.repository.PrayerTimeRepository
import com.ambercatalbas.vaktinde.core.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPrayerTimeRepository(
        impl: PrayerTimeRepositoryImpl,
    ): PrayerTimeRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl,
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl
    ): NotificationRepository
}

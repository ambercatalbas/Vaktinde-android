package com.ambercatalbas.vaktinde.notification

import com.ambercatalbas.vaktinde.core.domain.repository.NotificationScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationScheduler(
        impl: NotificationSchedulerImpl,
    ): NotificationScheduler
}

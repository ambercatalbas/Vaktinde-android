package com.ambercatalbas.vaktinde.core.data.repository

import com.ambercatalbas.vaktinde.core.data.datastore.NotificationPreferencesDataStore
import com.ambercatalbas.vaktinde.core.domain.model.NotificationMode
import com.ambercatalbas.vaktinde.core.domain.model.NotificationPreferences
import com.ambercatalbas.vaktinde.core.domain.model.PrayerType
import com.ambercatalbas.vaktinde.core.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val dataStore: NotificationPreferencesDataStore,
) : NotificationRepository {

    override val preferences: Flow<NotificationPreferences> = dataStore.preferences

    override suspend fun setMasterEnabled(enabled: Boolean) =
        dataStore.setMasterEnabled(enabled)

    override suspend fun setVibrationEnabled(enabled: Boolean) =
        dataStore.setVibrationEnabled(enabled)

    override suspend fun setFullAdhanEnabled(enabled: Boolean) =
        dataStore.setFullAdhanEnabled(enabled)

    override suspend fun setPreReminderMinutes(minutes: Int) =
        dataStore.setPreReminderMinutes(minutes)

    override suspend fun setPreReminderSoundId(soundId: String) =
        dataStore.setPreReminderSoundId(soundId)

    override suspend fun setPrayerMode(prayerType: PrayerType, mode: NotificationMode) =
        dataStore.setPrayerMode(prayerType, mode)

    override suspend fun setPrayerSound(prayerType: PrayerType, soundId: String) =
        dataStore.setPrayerSound(prayerType, soundId)
}

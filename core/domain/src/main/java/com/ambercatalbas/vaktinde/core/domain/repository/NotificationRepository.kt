package com.ambercatalbas.vaktinde.core.domain.repository

import com.ambercatalbas.vaktinde.core.domain.model.NotificationMode
import com.ambercatalbas.vaktinde.core.domain.model.NotificationPreferences
import com.ambercatalbas.vaktinde.core.domain.model.PrayerType
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    val preferences: Flow<NotificationPreferences>

    suspend fun setMasterEnabled(enabled: Boolean)
    suspend fun setVibrationEnabled(enabled: Boolean)
    suspend fun setFullAdhanEnabled(enabled: Boolean)
    suspend fun setPreReminderMinutes(minutes: Int)
    suspend fun setPreReminderSoundId(soundId: String)
    suspend fun setPrayerMode(prayerType: PrayerType, mode: NotificationMode)
    suspend fun setPrayerSound(prayerType: PrayerType, soundId: String)
}

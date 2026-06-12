package com.ambercatalbas.vaktinde.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ambercatalbas.vaktinde.core.domain.model.NotificationMode
import com.ambercatalbas.vaktinde.core.domain.model.NotificationPreferences
import com.ambercatalbas.vaktinde.core.domain.model.PrayerType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private object Keys {
        val MASTER_ENABLED = booleanPreferencesKey("notif_master")
        val VIBRATION_ENABLED = booleanPreferencesKey("notif_vibrate")
        val FULL_ADHAN_ENABLED = booleanPreferencesKey("notif_full_adhan")
        val PRE_REMINDER_MINUTES = intPreferencesKey("notif_pre_reminder")
        val PRE_REMINDER_SOUND = stringPreferencesKey("notif_pre_reminder_sound")

        fun prayerModeKey(prayerType: PrayerType) =
            stringPreferencesKey("notif_mode_${prayerType.key}")

        fun prayerSoundKey(prayerType: PrayerType) =
            stringPreferencesKey("notif_sound_${prayerType.key}")
    }

    val preferences: Flow<NotificationPreferences> = dataStore.data.map { prefs ->
        val prayerModes = PrayerType.entries.associateWith { type ->
            NotificationMode.fromKey(
                prefs[Keys.prayerModeKey(type)] ?: defaultModeFor(type).key
            )
        }
        val prayerSounds = PrayerType.entries.associateWith { type ->
            prefs[Keys.prayerSoundKey(type)] ?: defaultSoundFor(type)
        }

        NotificationPreferences(
            masterEnabled = prefs[Keys.MASTER_ENABLED] ?: true,
            vibrationEnabled = prefs[Keys.VIBRATION_ENABLED] ?: true,
            fullAdhanEnabled = prefs[Keys.FULL_ADHAN_ENABLED] ?: false,
            preReminderMinutes = prefs[Keys.PRE_REMINDER_MINUTES] ?: 0,
            preReminderSoundId = prefs[Keys.PRE_REMINDER_SOUND] ?: "sela",
            prayerModes = prayerModes,
            prayerSounds = prayerSounds,
        )
    }

    suspend fun setMasterEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.MASTER_ENABLED] = enabled }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.VIBRATION_ENABLED] = enabled }
    }

    suspend fun setFullAdhanEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.FULL_ADHAN_ENABLED] = enabled }
    }

    suspend fun setPreReminderMinutes(minutes: Int) {
        dataStore.edit { it[Keys.PRE_REMINDER_MINUTES] = minutes }
    }

    suspend fun setPreReminderSoundId(soundId: String) {
        dataStore.edit { it[Keys.PRE_REMINDER_SOUND] = soundId }
    }

    suspend fun setPrayerMode(prayerType: PrayerType, mode: NotificationMode) {
        dataStore.edit { it[Keys.prayerModeKey(prayerType)] = mode.key }
    }

    suspend fun setPrayerSound(prayerType: PrayerType, soundId: String) {
        dataStore.edit { it[Keys.prayerSoundKey(prayerType)] = soundId }
    }

    private fun defaultModeFor(type: PrayerType): NotificationMode = when (type) {
        PrayerType.IMSAK -> NotificationMode.SILENT
        PrayerType.GUNES -> NotificationMode.OFF
        else -> NotificationMode.ADHAN
    }

    private fun defaultSoundFor(type: PrayerType): String = when (type) {
        PrayerType.IMSAK, PrayerType.GUNES -> "sabah_ezan"
        PrayerType.OGLE -> "ogle_ezan"
        PrayerType.IKINDI -> "ikindi_ezan"
        PrayerType.AKSAM -> "aksam_ezan"
        PrayerType.YATSI -> "yatsi_ezan"
    }
}

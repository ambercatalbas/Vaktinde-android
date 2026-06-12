package com.ambercatalbas.vaktinde.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ambercatalbas.vaktinde.core.domain.model.NotificationMode
import com.ambercatalbas.vaktinde.core.domain.model.NotificationPreferences
import com.ambercatalbas.vaktinde.core.domain.model.Prayer
import com.ambercatalbas.vaktinde.core.domain.model.PrayerType
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        const val EXTRA_PRAYER_TYPE = "prayer_type"
        const val EXTRA_PRAYER_NAME = "prayer_name"
        const val EXTRA_PRAYER_TIME = "prayer_time"
        const val EXTRA_NOTIFICATION_MODE = "notification_mode"
        const val EXTRA_SOUND_FILE = "sound_file"
        const val EXTRA_IS_PRE_REMINDER = "is_pre_reminder"
        const val EXTRA_PRE_REMINDER_MINUTES = "pre_reminder_minutes"
    }

    fun scheduleAll(
        prayers: List<Prayer>,
        preferences: NotificationPreferences,
        date: LocalDate = LocalDate.now(),
    ) {
        cancelAll()

        if (!preferences.masterEnabled) return

        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return

        prayers.forEach { prayer ->
            val mode = preferences.modeFor(prayer.type)
            if (mode == NotificationMode.OFF) return@forEach

            val prayerTimeMillis = prayer.time
                .atDate(date)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            // Skip if prayer time has passed
            if (prayerTimeMillis <= System.currentTimeMillis()) return@forEach

            // Main prayer notification
            val mainIntent = createIntent(prayer, mode.key, preferences, false)
            val mainPending = PendingIntent.getBroadcast(
                context,
                prayer.type.ordinal,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    prayerTimeMillis,
                    mainPending,
                )
            } catch (_: SecurityException) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, prayerTimeMillis, mainPending)
            }

            // Pre-reminder notification
            if (preferences.preReminderMinutes > 0) {
                val preReminderMillis = prayerTimeMillis - (preferences.preReminderMinutes * 60 * 1000L)
                if (preReminderMillis > System.currentTimeMillis()) {
                    val preIntent = createIntent(prayer, mode.key, preferences, true)
                    val prePending = PendingIntent.getBroadcast(
                        context,
                        prayer.type.ordinal + 50,
                        preIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    )

                    try {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            preReminderMillis,
                            prePending,
                        )
                    } catch (_: SecurityException) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, preReminderMillis, prePending)
                    }
                }
            }
        }
    }

    fun cancelAll() {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        // Cancel main + pre-reminder for all 6 prayer types
        for (i in 0..5) {
            val mainIntent = Intent(context, PrayerAlarmReceiver::class.java)
            val mainPending = PendingIntent.getBroadcast(
                context, i, mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            alarmManager.cancel(mainPending)

            val preIntent = Intent(context, PrayerAlarmReceiver::class.java)
            val prePending = PendingIntent.getBroadcast(
                context, i + 50, preIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            alarmManager.cancel(prePending)
        }
    }

    private fun createIntent(
        prayer: Prayer,
        modeKey: String,
        preferences: NotificationPreferences,
        isPreReminder: Boolean,
    ): Intent {
        return Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra(EXTRA_PRAYER_TYPE, prayer.type.key)
            putExtra(EXTRA_PRAYER_NAME, prayer.type.key)
            putExtra(EXTRA_PRAYER_TIME, prayer.timeString)
            putExtra(EXTRA_NOTIFICATION_MODE, modeKey)
            putExtra(EXTRA_IS_PRE_REMINDER, isPreReminder)
            if (isPreReminder) {
                putExtra(EXTRA_PRE_REMINDER_MINUTES, preferences.preReminderMinutes)
                putExtra(EXTRA_SOUND_FILE, preferences.preReminderSoundId)
            } else {
                putExtra(EXTRA_SOUND_FILE, preferences.prayerSounds[prayer.type] ?: "")
            }
        }
    }
}

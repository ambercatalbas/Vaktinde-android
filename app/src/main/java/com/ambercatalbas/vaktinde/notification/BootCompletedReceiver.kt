package com.ambercatalbas.vaktinde.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ambercatalbas.vaktinde.core.domain.repository.NotificationRepository
import com.ambercatalbas.vaktinde.core.domain.repository.PrayerTimeRepository
import com.ambercatalbas.vaktinde.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject lateinit var scheduler: PrayerAlarmScheduler
    @Inject lateinit var prayerTimeRepository: PrayerTimeRepository
    @Inject lateinit var userPreferencesRepository: UserPreferencesRepository
    @Inject lateinit var notificationRepository: NotificationRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val city = userPreferencesRepository.selectedCity.first()
                val daily = prayerTimeRepository.getDailyPrayers(city).first()
                val prefs = notificationRepository.preferences.first()
                scheduler.scheduleAll(daily.prayers, prefs)
            } catch (_: Exception) {
                // Silent fail — will be rescheduled when app opens
            } finally {
                pendingResult.finish()
            }
        }
    }
}

package com.ambercatalbas.vaktinde.notification

import com.ambercatalbas.vaktinde.core.domain.model.Prayer
import com.ambercatalbas.vaktinde.core.domain.repository.NotificationRepository
import com.ambercatalbas.vaktinde.core.domain.repository.NotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationSchedulerImpl @Inject constructor(
    private val alarmScheduler: PrayerAlarmScheduler,
    private val notificationRepository: NotificationRepository,
) : NotificationScheduler {

    private var lastPrayers: List<Prayer>? = null

    init {
        // Observe preference changes and reschedule
        CoroutineScope(Dispatchers.IO).launch {
            notificationRepository.preferences.collect { prefs ->
                lastPrayers?.let { prayers ->
                    alarmScheduler.scheduleAll(prayers, prefs)
                }
            }
        }
    }

    override fun scheduleToday(prayers: List<Prayer>) {
        lastPrayers = prayers
        CoroutineScope(Dispatchers.IO).launch {
            val prefs = notificationRepository.preferences.first()
            alarmScheduler.scheduleAll(prayers, prefs)
        }
    }
}

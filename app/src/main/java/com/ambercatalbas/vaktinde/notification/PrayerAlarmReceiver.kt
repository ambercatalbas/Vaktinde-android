package com.ambercatalbas.vaktinde.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ambercatalbas.vaktinde.core.domain.model.NotificationMode
import com.ambercatalbas.vaktinde.core.domain.model.PrayerType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PrayerAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: PrayerNotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        val prayerTypeKey = intent.getStringExtra(PrayerAlarmScheduler.EXTRA_PRAYER_TYPE) ?: return
        val prayerType = PrayerType.fromKey(prayerTypeKey) ?: return
        val prayerTime = intent.getStringExtra(PrayerAlarmScheduler.EXTRA_PRAYER_TIME) ?: ""
        val modeKey = intent.getStringExtra(PrayerAlarmScheduler.EXTRA_NOTIFICATION_MODE) ?: return
        val mode = NotificationMode.fromKey(modeKey)
        val soundFile = intent.getStringExtra(PrayerAlarmScheduler.EXTRA_SOUND_FILE)
        val isPreReminder = intent.getBooleanExtra(PrayerAlarmScheduler.EXTRA_IS_PRE_REMINDER, false)
        val preReminderMinutes = intent.getIntExtra(PrayerAlarmScheduler.EXTRA_PRE_REMINDER_MINUTES, 0)

        val prayerName = prayerDisplayName(prayerType)

        if (isPreReminder) {
            notificationManager.showPreReminderNotification(
                prayerType = prayerType,
                prayerName = prayerName,
                minutesBefore = preReminderMinutes,
                soundFileName = soundFile,
            )
        } else {
            notificationManager.showPrayerNotification(
                prayerType = prayerType,
                prayerName = prayerName,
                prayerTime = prayerTime,
                mode = mode,
                soundFileName = soundFile,
            )
        }
    }

    private fun prayerDisplayName(type: PrayerType): String = when (type) {
        PrayerType.IMSAK -> "Imsak"
        PrayerType.GUNES -> "Gunes"
        PrayerType.OGLE -> "Ogle"
        PrayerType.IKINDI -> "Ikindi"
        PrayerType.AKSAM -> "Aksam"
        PrayerType.YATSI -> "Yatsi"
    }
}

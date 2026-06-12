package com.ambercatalbas.vaktinde.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.ambercatalbas.vaktinde.MainActivity
import com.ambercatalbas.vaktinde.R
import com.ambercatalbas.vaktinde.core.domain.model.NotificationMode
import com.ambercatalbas.vaktinde.core.domain.model.PrayerType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        const val CHANNEL_ADHAN = "prayer_adhan"
        const val CHANNEL_SILENT = "prayer_silent"
        const val CHANNEL_PRE_REMINDER = "prayer_pre_reminder"
    }

    fun createNotificationChannels() {
        val notificationManager = context.getSystemService(NotificationManager::class.java)

        val adhanChannel = NotificationChannel(
            CHANNEL_ADHAN,
            "Ezan Bildirimleri",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Namaz vakitlerinde ezan sesiyle bildirim"
            enableVibration(true)
        }

        val silentChannel = NotificationChannel(
            CHANNEL_SILENT,
            "Sessiz Bildirimler",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Ezan sesi olmadan namaz vakti bildirimi"
            setSound(null, null)
        }

        val preReminderChannel = NotificationChannel(
            CHANNEL_PRE_REMINDER,
            "On Hatirlatma",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Namaz vaktinden once hatirlatma"
        }

        notificationManager.createNotificationChannels(
            listOf(adhanChannel, silentChannel, preReminderChannel)
        )
    }

    fun showPrayerNotification(
        prayerType: PrayerType,
        prayerName: String,
        prayerTime: String,
        mode: NotificationMode,
        soundFileName: String?,
    ) {
        val channelId = when (mode) {
            NotificationMode.ADHAN -> CHANNEL_ADHAN
            NotificationMode.SILENT -> CHANNEL_SILENT
            NotificationMode.OFF -> return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, prayerType.ordinal, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(prayerName)
            .setContentText("$prayerName - $prayerTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (mode == NotificationMode.ADHAN && soundFileName != null) {
            val soundUri = getSoundUri(soundFileName)
            if (soundUri != null) {
                builder.setSound(soundUri)
            }
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(prayerType.ordinal + 100, builder.build())
    }

    fun showPreReminderNotification(
        prayerType: PrayerType,
        prayerName: String,
        minutesBefore: Int,
        soundFileName: String?,
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, prayerType.ordinal + 50, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_PRE_REMINDER)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("$prayerName - $minutesBefore dakika")
            .setContentText("$prayerName vaktine $minutesBefore dakika kaldi")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (soundFileName != null) {
            val soundUri = getSoundUri(soundFileName)
            if (soundUri != null) {
                builder.setSound(soundUri)
            }
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(prayerType.ordinal + 200, builder.build())
    }

    private fun getSoundUri(fileName: String): Uri? {
        val resName = fileName.removeSuffix(".mp3").removeSuffix(".caf").removeSuffix(".wav")
        val resId = context.resources.getIdentifier(resName, "raw", context.packageName)
        return if (resId != 0) {
            Uri.parse("android.resource://${context.packageName}/$resId")
        } else null
    }
}

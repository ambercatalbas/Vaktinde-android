package com.ambercatalbas.vaktinde

import android.app.Application
import com.ambercatalbas.vaktinde.notification.PrayerNotificationManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class VaktindeApplication : Application() {

    @Inject lateinit var notificationManager: PrayerNotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager.createNotificationChannels()
    }
}

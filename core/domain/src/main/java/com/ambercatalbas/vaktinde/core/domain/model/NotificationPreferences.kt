package com.ambercatalbas.vaktinde.core.domain.model

data class NotificationPreferences(
    val masterEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val fullAdhanEnabled: Boolean = false,
    val preReminderMinutes: Int = 0,
    val preReminderSoundId: String = "sela",
    val prayerModes: Map<PrayerType, NotificationMode> = mapOf(
        PrayerType.IMSAK to NotificationMode.SILENT,
        PrayerType.GUNES to NotificationMode.OFF,
        PrayerType.OGLE to NotificationMode.ADHAN,
        PrayerType.IKINDI to NotificationMode.ADHAN,
        PrayerType.AKSAM to NotificationMode.ADHAN,
        PrayerType.YATSI to NotificationMode.ADHAN,
    ),
    val prayerSounds: Map<PrayerType, String> = mapOf(
        PrayerType.IMSAK to "sabah_ezan",
        PrayerType.GUNES to "sabah_ezan",
        PrayerType.OGLE to "ogle_ezan",
        PrayerType.IKINDI to "ikindi_ezan",
        PrayerType.AKSAM to "aksam_ezan",
        PrayerType.YATSI to "yatsi_ezan",
    ),
) {
    fun modeFor(prayerType: PrayerType): NotificationMode =
        prayerModes[prayerType] ?: NotificationMode.ADHAN

    fun soundFor(prayerType: PrayerType): AdhanSound =
        AdhanSound.fromId(prayerSounds[prayerType] ?: AdhanSound.DEFAULT.id)
}

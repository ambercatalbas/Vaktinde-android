package com.ambercatalbas.vaktinde.core.domain.model

data class PrayerDayTimes(
    val date: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
) {
    fun time(type: PrayerType): String = when (type) {
        PrayerType.IMSAK -> fajr
        PrayerType.GUNES -> sunrise
        PrayerType.OGLE -> dhuhr
        PrayerType.IKINDI -> asr
        PrayerType.AKSAM -> maghrib
        PrayerType.YATSI -> isha
    }
}

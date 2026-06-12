package com.ambercatalbas.vaktinde.core.domain.model

data class PrayerState(
    val currentPrayer: PrayerType,
    val nextPrayer: PrayerType,
    val nextPrayerTime: Prayer,
    val remainingSeconds: Long,
    val progress: Double,
) {
    val formattedCountdown: CountdownParts
        get() {
            val hours = remainingSeconds / 3600
            val minutes = (remainingSeconds % 3600) / 60
            val seconds = remainingSeconds % 60
            return CountdownParts(
                hours = "%02d".format(hours),
                minutes = "%02d".format(minutes),
                seconds = "%02d".format(seconds),
            )
        }
}

data class CountdownParts(
    val hours: String,
    val minutes: String,
    val seconds: String,
)

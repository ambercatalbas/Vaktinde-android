package com.ambercatalbas.vaktinde.core.domain.model

import java.time.LocalTime

data class Prayer(
    val type: PrayerType,
    val time: LocalTime,
) {
    val timeString: String
        get() = "%02d:%02d".format(time.hour, time.minute)
}

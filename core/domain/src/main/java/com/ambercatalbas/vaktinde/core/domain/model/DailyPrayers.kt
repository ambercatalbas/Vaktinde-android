package com.ambercatalbas.vaktinde.core.domain.model

import java.time.LocalDate

data class DailyPrayers(
    val date: LocalDate,
    val prayers: List<Prayer>,
    val hijriDate: String = "",
    val gregorianDate: String = "",
) {
    fun prayer(type: PrayerType): Prayer? = prayers.find { it.type == type }
}

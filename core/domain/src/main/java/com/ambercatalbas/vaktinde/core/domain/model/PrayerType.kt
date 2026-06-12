package com.ambercatalbas.vaktinde.core.domain.model

enum class PrayerType(val key: String) {
    IMSAK("imsak"),
    GUNES("gunes"),
    OGLE("ogle"),
    IKINDI("ikindi"),
    AKSAM("aksam"),
    YATSI("yatsi");

    companion object {
        fun fromKey(key: String): PrayerType? = entries.find { it.key == key }
    }
}

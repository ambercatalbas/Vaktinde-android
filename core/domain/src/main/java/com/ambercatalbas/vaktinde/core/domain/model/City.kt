package com.ambercatalbas.vaktinde.core.domain.model

import java.util.TimeZone

data class City(
    val id: String,
    val name: String,
    val region: String,
    val latitude: Double,
    val longitude: Double,
    val timeZoneIdentifier: String? = null,
    val diyanetDistrictId: String? = null,
    val countryCode: String? = null,
) {
    val resolvedTimeZone: TimeZone
        get() = timeZoneIdentifier?.let { TimeZone.getTimeZone(it) }
            ?: estimateTimeZone(latitude, longitude)

    val isTurkey: Boolean
        get() = countryCode == "TR" || isTurkeyCoordinate(latitude, longitude)

    val prayerLocationId: String
        get() = if (diyanetDistrictId != null) {
            "diyanet_$diyanetDistrictId"
        } else {
            "$latitude,$longitude"
        }

    companion object {
        val default = City(
            id = "istanbul",
            name = "İstanbul",
            region = "Türkiye",
            latitude = 41.0082,
            longitude = 28.9784,
            timeZoneIdentifier = "Europe/Istanbul",
            diyanetDistrictId = "9541",
            countryCode = "TR",
        )

        val popular = listOf(
            default,
            City("ankara", "Ankara", "Türkiye", 39.9334, 32.8597, "Europe/Istanbul", "9206", "TR"),
            City("izmir", "İzmir", "Türkiye", 38.4192, 27.1287, "Europe/Istanbul", "9560", "TR"),
            City("bursa", "Bursa", "Türkiye", 40.1885, 29.0610, "Europe/Istanbul", "9335", "TR"),
            City("antalya", "Antalya", "Türkiye", 36.8969, 30.7133, "Europe/Istanbul", "9225", "TR"),
            City("konya", "Konya", "Türkiye", 37.8746, 32.4932, "Europe/Istanbul", "9676", "TR"),
            City("gaziantep", "Gaziantep", "Türkiye", 37.0662, 37.3833, "Europe/Istanbul", "9461", "TR"),
            City("mecca", "Mekke", "Suudi Arabistan", 21.4225, 39.8262, "Asia/Riyadh", null, "SA"),
            City("medina", "Medine", "Suudi Arabistan", 24.4672, 39.6024, "Asia/Riyadh", null, "SA"),
            City("jerusalem", "Kudüs", "Filistin", 31.7683, 35.2137, "Asia/Jerusalem", null, "PS"),
            City("berlin", "Berlin", "Almanya", 52.5200, 13.4050, "Europe/Berlin", null, "DE"),
            City("london", "Londra", "İngiltere", 51.5074, -0.1278, "Europe/London", null, "GB"),
        )

        fun estimateTimeZone(latitude: Double, longitude: Double): TimeZone {
            if (isTurkeyCoordinate(latitude, longitude)) return TimeZone.getTimeZone("Europe/Istanbul")
            if (latitude in 15.0..32.0 && longitude in 34.0..56.0) return TimeZone.getTimeZone("Asia/Riyadh")
            if (latitude in 22.0..31.5 && longitude in 25.0..35.0) return TimeZone.getTimeZone("Africa/Cairo")
            if (latitude in 47.0..55.0 && longitude in 5.5..15.0) return TimeZone.getTimeZone("Europe/Berlin")
            if (latitude in 49.0..61.0 && longitude in -8.0..2.0) return TimeZone.getTimeZone("Europe/London")
            if (latitude in 41.0..51.5 && longitude in -5.5..9.5) return TimeZone.getTimeZone("Europe/Paris")

            val offsetHours = (longitude / 15.0).toInt()
            val tzId = "GMT%+d".format(offsetHours)
            return TimeZone.getTimeZone(tzId)
        }

        fun isTurkeyCoordinate(latitude: Double, longitude: Double): Boolean {
            return latitude in 36.0..42.5 && longitude in 26.0..45.0
        }
    }
}

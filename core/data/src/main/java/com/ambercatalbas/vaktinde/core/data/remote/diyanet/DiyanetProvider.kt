package com.ambercatalbas.vaktinde.core.data.remote.diyanet

import com.ambercatalbas.vaktinde.core.domain.model.PrayerDayTimes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiyanetProvider @Inject constructor(
    private val api: DiyanetApiService,
) {
    suspend fun fetchMonthlyTimes(districtId: String): List<PrayerDayTimes> {
        val response = api.getMonthlyTimes(districtId)

        if (!response.success) {
            throw Exception("Diyanet API error")
        }

        return response.data.map { day ->
            PrayerDayTimes(
                date = day.date,
                fajr = day.times.imsak,
                sunrise = day.times.gunes,
                dhuhr = day.times.ogle,
                asr = day.times.ikindi,
                maghrib = day.times.aksam,
                isha = day.times.yatsi,
            )
        }
    }

    suspend fun searchLocation(query: String): List<DiyanetLocation> {
        val response = api.searchLocation(query)
        return if (response.success) response.data else emptyList()
    }
}

package com.ambercatalbas.vaktinde.core.data.remote.aladhan

import com.ambercatalbas.vaktinde.core.domain.model.CalcMethod
import com.ambercatalbas.vaktinde.core.domain.model.PrayerDayTimes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AladhanProvider @Inject constructor(
    private val api: AladhanApiService,
) {
    suspend fun fetchMonthlyTimes(
        latitude: Double,
        longitude: Double,
        month: Int,
        year: Int,
        method: CalcMethod,
    ): List<PrayerDayTimes> {
        val response = api.getMonthlyTimes(
            year = year,
            month = month,
            latitude = latitude,
            longitude = longitude,
            method = method.aladhanId,
        )

        return response.data.map { day ->
            PrayerDayTimes(
                date = convertDateFormat(day.date.gregorian.date),
                fajr = cleanTime(day.timings.fajr),
                sunrise = cleanTime(day.timings.sunrise),
                dhuhr = cleanTime(day.timings.dhuhr),
                asr = cleanTime(day.timings.asr),
                maghrib = cleanTime(day.timings.maghrib),
                isha = cleanTime(day.timings.isha),
            )
        }
    }

    private fun cleanTime(time: String): String {
        // Aladhan returns times like "05:03 (EET)" - strip timezone info
        return time.split(" ").first().trim()
    }

    private fun convertDateFormat(date: String): String {
        // Convert DD-MM-YYYY to yyyy-MM-dd
        val parts = date.split("-")
        if (parts.size == 3) {
            return "${parts[2]}-${parts[1]}-${parts[0]}"
        }
        return date
    }
}

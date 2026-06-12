package com.ambercatalbas.vaktinde.core.data.repository

import com.ambercatalbas.vaktinde.core.data.local.dao.PrayerTimeDao
import com.ambercatalbas.vaktinde.core.data.local.entity.PrayerTimeEntity
import com.ambercatalbas.vaktinde.core.data.remote.aladhan.AladhanProvider
import com.ambercatalbas.vaktinde.core.data.remote.diyanet.DiyanetProvider
import com.ambercatalbas.vaktinde.core.domain.model.CalcMethod
import com.ambercatalbas.vaktinde.core.domain.model.City
import com.ambercatalbas.vaktinde.core.domain.model.DailyPrayers
import com.ambercatalbas.vaktinde.core.domain.model.Prayer
import com.ambercatalbas.vaktinde.core.domain.model.PrayerDayTimes
import com.ambercatalbas.vaktinde.core.domain.model.PrayerType
import com.ambercatalbas.vaktinde.core.domain.repository.PrayerTimeRepository
import com.ambercatalbas.vaktinde.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerTimeRepositoryImpl @Inject constructor(
    private val prayerTimeDao: PrayerTimeDao,
    private val diyanetProvider: DiyanetProvider,
    private val aladhanProvider: AladhanProvider,
    private val userPreferencesRepository: UserPreferencesRepository,
) : PrayerTimeRepository {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun getDailyPrayers(city: City): Flow<DailyPrayers> = flow {
        val today = LocalDate.now()
        val dateStr = today.format(dateFormatter)

        // Try cache first
        val cached = prayerTimeDao.getByDate(city.prayerLocationId, dateStr)
        if (cached != null) {
            emit(entityToDailyPrayers(cached, today))
            return@flow
        }

        // Fetch from API
        val monthlyTimes = fetchFromApi(city, today.monthValue, today.year)
        cacheResults(city.prayerLocationId, monthlyTimes)

        val todayTimes = monthlyTimes.find { it.date == dateStr }
        if (todayTimes != null) {
            emit(dayTimesToDailyPrayers(todayTimes, today))
        }
    }

    override suspend fun getMonthlyPrayers(city: City, month: Int, year: Int): List<PrayerDayTimes> {
        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.withDayOfMonth(startDate.lengthOfMonth())

        // Try cache
        val cached = prayerTimeDao.getByDateRange(
            city.prayerLocationId,
            startDate.format(dateFormatter),
            endDate.format(dateFormatter),
        )
        if (cached.size >= startDate.lengthOfMonth()) {
            return cached.map { entityToPrayerDayTimes(it) }
        }

        // Fetch from API
        val results = fetchFromApi(city, month, year)
        cacheResults(city.prayerLocationId, results)
        return results
    }

    override suspend fun refreshPrayers(city: City) {
        val today = LocalDate.now()
        val monthlyTimes = fetchFromApi(city, today.monthValue, today.year)
        cacheResults(city.prayerLocationId, monthlyTimes)
    }

    private suspend fun fetchFromApi(city: City, month: Int, year: Int): List<PrayerDayTimes> {
        return if (city.diyanetDistrictId != null) {
            try {
                diyanetProvider.fetchMonthlyTimes(city.diyanetDistrictId!!)
            } catch (_: Exception) {
                fetchFromAladhan(city, month, year)
            }
        } else {
            fetchFromAladhan(city, month, year)
        }
    }

    private suspend fun fetchFromAladhan(city: City, month: Int, year: Int): List<PrayerDayTimes> {
        val method = userPreferencesRepository.calcMethod.first()
        return aladhanProvider.fetchMonthlyTimes(
            latitude = city.latitude,
            longitude = city.longitude,
            month = month,
            year = year,
            method = method,
        )
    }

    private suspend fun cacheResults(locationId: String, times: List<PrayerDayTimes>) {
        val entities = times.map { day ->
            PrayerTimeEntity(
                id = "${locationId}_${day.date}",
                locationId = locationId,
                date = day.date,
                fajr = day.fajr,
                sunrise = day.sunrise,
                dhuhr = day.dhuhr,
                asr = day.asr,
                maghrib = day.maghrib,
                isha = day.isha,
            )
        }
        prayerTimeDao.insertAll(entities)
    }

    private fun entityToDailyPrayers(entity: PrayerTimeEntity, date: LocalDate): DailyPrayers {
        return DailyPrayers(
            date = date,
            prayers = listOf(
                Prayer(PrayerType.IMSAK, parseTime(entity.fajr)),
                Prayer(PrayerType.GUNES, parseTime(entity.sunrise)),
                Prayer(PrayerType.OGLE, parseTime(entity.dhuhr)),
                Prayer(PrayerType.IKINDI, parseTime(entity.asr)),
                Prayer(PrayerType.AKSAM, parseTime(entity.maghrib)),
                Prayer(PrayerType.YATSI, parseTime(entity.isha)),
            ),
        )
    }

    private fun dayTimesToDailyPrayers(dayTimes: PrayerDayTimes, date: LocalDate): DailyPrayers {
        return DailyPrayers(
            date = date,
            prayers = PrayerType.entries.map { type ->
                Prayer(type, parseTime(dayTimes.time(type)))
            },
        )
    }

    private fun entityToPrayerDayTimes(entity: PrayerTimeEntity): PrayerDayTimes {
        return PrayerDayTimes(
            date = entity.date,
            fajr = entity.fajr,
            sunrise = entity.sunrise,
            dhuhr = entity.dhuhr,
            asr = entity.asr,
            maghrib = entity.maghrib,
            isha = entity.isha,
        )
    }

    private fun parseTime(timeStr: String): LocalTime {
        return try {
            LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"))
        } catch (_: Exception) {
            LocalTime.MIDNIGHT
        }
    }
}

package com.ambercatalbas.vaktinde.core.domain.repository

import com.ambercatalbas.vaktinde.core.domain.model.City
import com.ambercatalbas.vaktinde.core.domain.model.DailyPrayers
import com.ambercatalbas.vaktinde.core.domain.model.PrayerDayTimes
import kotlinx.coroutines.flow.Flow

interface PrayerTimeRepository {
    fun getDailyPrayers(city: City): Flow<DailyPrayers>
    suspend fun getMonthlyPrayers(city: City, month: Int, year: Int): List<PrayerDayTimes>
    suspend fun refreshPrayers(city: City)
}

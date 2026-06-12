package com.ambercatalbas.vaktinde.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ambercatalbas.vaktinde.core.data.local.entity.PrayerTimeEntity

@Dao
interface PrayerTimeDao {

    @Query("SELECT * FROM prayer_times WHERE locationId = :locationId AND date = :date LIMIT 1")
    suspend fun getByDate(locationId: String, date: String): PrayerTimeEntity?

    @Query("SELECT * FROM prayer_times WHERE locationId = :locationId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getByDateRange(locationId: String, startDate: String, endDate: String): List<PrayerTimeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(times: List<PrayerTimeEntity>)

    @Query("DELETE FROM prayer_times WHERE cachedAt < :beforeTimestamp")
    suspend fun deleteOlderThan(beforeTimestamp: Long)

    @Query("DELETE FROM prayer_times WHERE locationId = :locationId")
    suspend fun deleteByLocation(locationId: String)
}

package com.ambercatalbas.vaktinde.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_times")
data class PrayerTimeEntity(
    @PrimaryKey
    val id: String, // "{locationId}_{date}"
    val locationId: String,
    val date: String, // yyyy-MM-dd
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val cachedAt: Long = System.currentTimeMillis(),
)

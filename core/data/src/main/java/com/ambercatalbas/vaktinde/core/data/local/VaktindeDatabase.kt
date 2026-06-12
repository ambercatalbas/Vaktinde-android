package com.ambercatalbas.vaktinde.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ambercatalbas.vaktinde.core.data.local.dao.PrayerTimeDao
import com.ambercatalbas.vaktinde.core.data.local.entity.PrayerTimeEntity

@Database(
    entities = [PrayerTimeEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class VaktindeDatabase : RoomDatabase() {
    abstract fun prayerTimeDao(): PrayerTimeDao
}

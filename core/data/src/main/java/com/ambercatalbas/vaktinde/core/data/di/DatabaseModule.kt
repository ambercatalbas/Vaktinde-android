package com.ambercatalbas.vaktinde.core.data.di

import android.content.Context
import androidx.room.Room
import com.ambercatalbas.vaktinde.core.data.local.VaktindeDatabase
import com.ambercatalbas.vaktinde.core.data.local.dao.PrayerTimeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VaktindeDatabase {
        return Room.databaseBuilder(
            context,
            VaktindeDatabase::class.java,
            "vaktinde_db",
        ).build()
    }

    @Provides
    @Singleton
    fun providePrayerTimeDao(db: VaktindeDatabase): PrayerTimeDao {
        return db.prayerTimeDao()
    }
}

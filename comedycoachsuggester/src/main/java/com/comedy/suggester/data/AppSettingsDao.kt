package com.comedy.suggester.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO for AppSetting
 */
@Dao
interface AppSettingsDao {
    // Insert, or update if exists.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(appSettings: AppSettings)


    // Get a single setting of the given ID
    @Query("SELECT * from ${AppSettings.TABLE_NAME} WHERE id = :settingId")
    fun getSetting(settingId: String): Flow<AppSettings>
}
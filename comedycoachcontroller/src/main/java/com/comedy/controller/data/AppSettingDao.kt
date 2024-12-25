package com.comedy.controller.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.comedy.controller.data.AppSetting.Companion.DEFAULT_ID
import kotlinx.coroutines.flow.Flow

/*
DAO for AppSetting
 */
@Dao
interface AppSettingDao {
    // Insert, or update if exists.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(appSetting: AppSetting)


    // Get a single setting of the given ID
    @Query("SELECT * from ${AppSetting.TABLE_NAME} WHERE id = :settingId")
    fun getSetting(settingId: String): Flow<AppSetting>

    // Get the default setting.
    fun getDefaultSetting(): Flow<AppSetting> {
        return getSetting(DEFAULT_ID)
    }

    // Update Open Ai Api Key
    suspend fun updateDefaultOpenAiApiKey(openAiApiKey: String) {
        upsert(AppSetting(id = DEFAULT_ID, openAiApiKey = openAiApiKey))
    }
}
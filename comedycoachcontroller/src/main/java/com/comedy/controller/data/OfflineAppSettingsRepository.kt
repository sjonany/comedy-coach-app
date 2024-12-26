package com.comedy.controller.data

import com.comedy.controller.data.AppSettings.Companion.MAIN_ID
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of app settings repo where we use the DAO
 */
class OfflineAppSettingsRepository(private val appSettingsDao: AppSettingsDao) :
    AppSettingsRepository {
    override fun getMainSettings(): Flow<AppSettings> = appSettingsDao.getSetting(MAIN_ID)

    override suspend fun updateOpenAiApiKey(openAiApiKey: String) =
        appSettingsDao.upsert(AppSettings(openAiApiKey = openAiApiKey))
}
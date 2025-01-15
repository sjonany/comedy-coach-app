package com.comedy.suggester.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository for AppSettings
 */
interface AppSettingsRepository {
    /**
     * Get the main settings
     */
    fun getMainSettings(): Flow<AppSettings?>

    /**
     * Update settings
     */
    suspend fun updateSettings(appSettings: AppSettings)
}
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
     * Update Open Ai Api Key in main settings
     */
    suspend fun updateOpenAiApiKey(openAiApiKey: String)
}
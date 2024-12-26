package com.comedy.controller.data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val appSettingsRepository: AppSettingsRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineAppSettingsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [AppSettingsRepository]
     */
    override val appSettingsRepository: AppSettingsRepository by lazy {
        OfflineAppSettingsRepository(AppDatabase.getDatabase(context).appSettingDao())
    }
}
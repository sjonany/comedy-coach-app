package com.comedy.suggester.data

import android.content.Context

/**
 * App container for Dependency injection.
 * Adapted from https://github.com/google-developer-training/basic-android-kotlin-compose-training-inventory-app/blob/room/app/src/main/java/com/example/inventory/ui/AppViewModelProvider.kt
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
        OfflineAppSettingsRepository(AppDatabase.getDatabase(context).appSettingsDao())
    }
}

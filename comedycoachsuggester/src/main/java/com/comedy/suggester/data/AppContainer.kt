package com.comedy.suggester.data

import android.content.Context
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI

/**
 * App container for Dependency injection.
 * Adapted from https://github.com/google-developer-training/basic-android-kotlin-compose-training-inventory-app/blob/room/app/src/main/java/com/example/inventory/ui/AppViewModelProvider.kt
 */
interface AppContainer {
    val appSettingsRepository: AppSettingsRepository
    var openAiApiService: OpenAI?

    fun initializeOpenAiApiService(apiKey: String)
}

/**
 * [AppContainer] implementation
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [AppSettingsRepository]
     */
    override val appSettingsRepository: AppSettingsRepository by lazy {
        OfflineAppSettingsRepository(AppDatabase.getDatabase(context).appSettingsDao())
    }

    /**
     * Before accessing this field, call initializeOpenAiApiService
     */
    override var openAiApiService: OpenAI? = null
        get() = field ?: throw IllegalStateException("OpenAI API Service is not initialized yet.")

    override fun initializeOpenAiApiService(apiKey: String) {
        openAiApiService = OpenAI(
            token = apiKey,
            logging = LoggingConfig(LogLevel.All)
        )
    }
}

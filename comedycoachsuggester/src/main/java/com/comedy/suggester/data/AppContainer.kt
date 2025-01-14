package com.comedy.suggester.data

import android.content.Context
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.anthropic.client.AnthropicClient
import com.anthropic.client.okhttp.AnthropicOkHttpClient

/**
 * App container for Dependency injection.
 * Adapted from https://github.com/google-developer-training/basic-android-kotlin-compose-training-inventory-app/blob/room/app/src/main/java/com/example/inventory/ui/AppViewModelProvider.kt
 */
interface AppContainer {
    val appSettingsRepository: AppSettingsRepository
    val characterProfileRepository: CharacterProfileRepository
    var openAiClient: OpenAI?
    var anthropicClient: AnthropicClient?

    fun initializeOpenAiApiService(apiKey: String)
    fun initializeAnthropicClient(apiKey: String)
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
     * Implementation for [CharacterProfileRepository]
     */
    override val characterProfileRepository: CharacterProfileRepository by lazy {
        OfflineCharacterProfileRepository(AppDatabase.getDatabase(context).characterProfileDao())
    }

    /**
     * Before accessing this field, call initializeOpenAiApiService
     */
    override var openAiClient: OpenAI? = null
        get() = field ?: throw IllegalStateException("OpenAI API Service is not initialized yet.")

    override fun initializeOpenAiApiService(apiKey: String) {
        openAiClient = OpenAI(
            token = apiKey,
            logging = LoggingConfig(LogLevel.All)
        )
    }

    /**
     * Before accessing this field, call initializeOpenAiApiService
     */
    override var anthropicClient: AnthropicClient? = null
        get() = field
            ?: throw IllegalStateException("Anthropic API Service is not initialized yet.")

    override fun initializeAnthropicClient(apiKey: String) {
        anthropicClient =
            AnthropicOkHttpClient.builder().apiKey(apiKey).build()
    }
}

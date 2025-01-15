package com.comedy.suggester.ui.appsetting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comedy.suggester.data.AppSettings
import com.comedy.suggester.data.AppSettingsRepository
import com.comedy.suggester.data.LlmModel
import kotlinx.coroutines.launch

/**
 * ViewModel to update AppSettings in Room db.
 * Here are the related core objects:
 * - UI state: AppSettingsUiState -- UI concept and more for the screen state. UI details and more.
 * - UI details: AppSettingsDetails -- UI representation of the DB concepts
 * - DB: AppSettings -- DAO deals with this.
 */
class AppSettingsViewModel(private val appSettingsRepository: AppSettingsRepository) : ViewModel() {
    /**
     * Holds current ui state
     */
    var appSettingsUiState by mutableStateOf(AppSettingsUiState())
        private set

    // Initialize the value w/ what we have from the db
    init {
        loadAppSettings()
    }

    // Load app settings from the repo
    private fun loadAppSettings() {
        viewModelScope.launch {
            appSettingsRepository.getMainSettings().collect { repoSettings ->
                // Update the UI state when the flow emits a new value
                appSettingsUiState = repoSettings?.toUiState() ?: AppSettingsUiState()
            }
        }
    }


    /**
     * Updates the [appSettingsUiState] with the value provided in the argument.
     */
    fun updateUiState(appSettingsDetails: AppSettingsDetails) {
        appSettingsUiState =
            AppSettingsUiState(appSettingsDetails = appSettingsDetails)
    }

    /** Save the current ui state to db. */
    suspend fun saveAppSettings() {
        appSettingsRepository.updateSettings(
            appSettingsUiState.appSettingsDetails.toDb()
        )
    }
}

/**
 * Represents Ui State for an AppSettings.
 */
data class AppSettingsUiState(
    val appSettingsDetails: AppSettingsDetails = AppSettingsDetails()
)

/**
 * UI concept of an App setting. Complementary to the db representation.
 * We suffix the db class name with "details"
 */
data class AppSettingsDetails(
    val openAiApiKey: String = "",
    val anthropicApiKey: String = "",
    val llmModel: LlmModel = LlmModel.DEFAULT
)


/**
 * Extension function to convert ui to db representation.
 */
fun AppSettingsDetails.toDb(): AppSettings = AppSettings(
    openAiApiKey = openAiApiKey,
    anthropicApiKey = anthropicApiKey,
    llmModel = llmModel
)


/**
 * Extension function to convert db to ui state
 */
fun AppSettings.toUiState(): AppSettingsUiState = AppSettingsUiState(
    appSettingsDetails = this.toUiDetails(),
)

/**
 * Extension function to convert db to ui details
 */
fun AppSettings.toUiDetails(): AppSettingsDetails = AppSettingsDetails(
    openAiApiKey = openAiApiKey,
    anthropicApiKey = anthropicApiKey,
    llmModel = llmModel
)
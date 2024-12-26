package com.comedy.controller.ui.appsetting

import androidx.lifecycle.ViewModel
import com.comedy.controller.data.AppSettings
import com.comedy.controller.data.AppSettingsRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

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

    /**
     * Updates the [appSettingsUiState] with the value provided in the argument.
     */
    fun updateUiState(appSettingsDetails: AppSettingsDetails) {
        appSettingsUiState =
            AppSettingsUiState(appSettingsDetails = appSettingsDetails)
    }

    /** Save the current ui state to db. */
    suspend fun saveAppSettings() {
            appSettingsRepository.updateOpenAiApiKey(
                appSettingsUiState.appSettingsDetails.toDb().openAiApiKey)
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
    val openAiApiKey: String = ""
)


/**
 * Extension function to convert ui to db representation.
 */
fun AppSettingsDetails.toDb(): AppSettings = AppSettings(
    openAiApiKey = openAiApiKey
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
    openAiApiKey = openAiApiKey
)
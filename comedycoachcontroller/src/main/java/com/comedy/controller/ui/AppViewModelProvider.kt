package com.comedy.controller.ui

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.comedy.controller.data.controllerApplication
import com.comedy.controller.ui.appsetting.AppSettingsViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for AppSettingsViewModel
        initializer {
            AppSettingsViewModel(
                controllerApplication().container.appSettingsRepository
            )
        }
    }
}

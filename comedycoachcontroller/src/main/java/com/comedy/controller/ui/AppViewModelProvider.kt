package com.comedy.controller.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.comedy.controller.ControllerApplication
import com.comedy.controller.ui.appsetting.AppSettingsViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire controller app
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

fun CreationExtras.controllerApplication(): ControllerApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ControllerApplication)

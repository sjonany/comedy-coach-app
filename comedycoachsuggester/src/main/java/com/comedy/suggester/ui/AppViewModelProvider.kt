package com.comedy.suggester.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.comedy.suggester.SuggesterApplication
import com.comedy.suggester.ui.appsetting.AppSettingsViewModel
import com.comedy.suggester.ui.characterselection.CharacterSelectionViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire controller app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AppSettingsViewModel(
                suggesterApplication().container.appSettingsRepository
            )
        }
        initializer {
            CharacterSelectionViewModel(
                suggesterApplication().container.characterProfileRepository
            )
        }
    }
}

fun CreationExtras.suggesterApplication(): SuggesterApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SuggesterApplication)

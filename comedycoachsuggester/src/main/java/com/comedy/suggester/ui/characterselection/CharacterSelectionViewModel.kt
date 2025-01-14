package com.comedy.suggester.ui.characterselection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comedy.suggester.data.CharacterProfile
import com.comedy.suggester.data.CharacterProfileRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for character selection
 * Here are the related core objects:
 * - UI state: CharacterSelectionsUiState -- UI concept and more for the screen state. UI details and more.
 * - UI details: CharacterSelectionDetails -- UI representation of the DB concepts
 * - DB: CharacterProfile -- DAO deals with this.
 */
class CharacterSelectionViewModel(private val characterProfileRepository: CharacterProfileRepository) :
    ViewModel() {
    /**
     * Holds current ui state
     */
    var characterSelectionUiState by mutableStateOf(CharacterSelectionUiState())
        private set

    // Initialize the value w/ what we have from the db
    init {
        reloadCharacterProfiles()
    }

    // Load from the repo
    fun reloadCharacterProfiles() {
        viewModelScope.launch {
            characterProfileRepository.getAllCharacterProfiles().collect { characterProfiles ->
                // Update the UI state when the flow emits a new value
                characterSelectionUiState =
                    dbToUiState(characterProfiles)
            }
        }
    }

    /** Create a new user. */
    suspend fun createNewUser(newUser: String) {
        characterProfileRepository.createNewProfile(
            CharacterProfile(
                id = newUser,
                senseOfHumor = "",
                aliases = mapOf()
            )
        )
    }
}

data class CharacterSelectionUiState(
    val characterProfiles: List<CharacterProfile> = emptyList()
)

/**
 * Extension function to convert db to ui state
 */
fun dbToUiState(characterProfiles: List<CharacterProfile>): CharacterSelectionUiState =
    CharacterSelectionUiState(
        characterProfiles = characterProfiles,
    )

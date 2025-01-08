package com.comedy.suggester.ui.charactereditor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comedy.suggester.ChatWatcherAccessibilityService
import com.comedy.suggester.data.CharacterProfile
import com.comedy.suggester.data.CharacterProfileRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for character editor
 */
class CharacterEditorViewModel(
    private val characterId: String,
    private val characterProfileRepository: CharacterProfileRepository
) :
    ViewModel() {
    /**
     * Holds current ui state
     */
    var characterEditorUiState by mutableStateOf(
        CharacterEditorUiState(
            CharacterProfile(
                id = characterId,
                description = "",
                aliases = mapOf()
            )
        )
    )
        private set

    // Initialize the value w/ what we have from the db
    init {
        reloadCharacterProfile()
    }

    // Load from the repo
    fun reloadCharacterProfile() {
        viewModelScope.launch {
            characterProfileRepository.findCharacterProfileById(characterId)
                .collect { characterProfile ->
                    characterEditorUiState =
                        dbToUiState(characterProfile!!)
                }
        }
    }

    /** Create a new user. */
    suspend fun saveCharacterProfile(characterProfile: CharacterProfile) {
        characterProfileRepository.updateProfile(
            characterProfile
        )
    }
}

data class CharacterEditorUiState(
    val characterProfile: CharacterProfile
)

/**
 * Extension function to convert db to ui state
 */
fun dbToUiState(characterProfile: CharacterProfile): CharacterEditorUiState =
    CharacterEditorUiState(
        characterProfile = characterProfile,
    )

val ALIAS_SEPARATOR = ","

/**
 * Return a list of aliases to the format displayed in the UI.
 * Right now it's comma separated, so we can't handle names that have commas.
 */
fun getAliasesAsUiString(characterProfile: CharacterProfile, packageName: String): String {
    return characterProfile.aliases[packageName]?.joinToString(
        separator = ALIAS_SEPARATOR
    ) ?: ""
}

fun fromAliasUiToMap(
    discordAliasUi: String,
    whatsappAliasUi: String
): Map<String, List<String>> {
    return mapOf(
        ChatWatcherAccessibilityService.DISCORD_PACKAGE to discordAliasUi.split(ALIAS_SEPARATOR),
        ChatWatcherAccessibilityService.WHATSAPP_PACKAGE to whatsappAliasUi.split(
            ALIAS_SEPARATOR
        )
    )
}
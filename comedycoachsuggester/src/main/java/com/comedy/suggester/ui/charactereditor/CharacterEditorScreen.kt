package com.comedy.suggester.ui.charactereditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.comedy.suggester.ChatWatcherAccessibilityService
import com.comedy.suggester.SuggesterApplication
import com.comedy.suggester.data.CharacterProfile
import com.comedy.suggester.generator.PromptStrings
import com.comedy.suggester.ui.AppViewModelProvider
import com.comedy.suggester.ui.common.showText
import kotlinx.coroutines.launch


private const val LOG_TAG = "CharacterEditorScreen"

/**
 * Screen for configuring 1 character profile.
 */
@Composable
fun CharacterEditorScreen(
    modifier: Modifier,
    navController: NavHostController,
    characterId: String,
    viewModel: CharacterEditorViewModel = viewModel(
        factory = AppViewModelProvider.Factory,
        extras = MutableCreationExtras().apply {
            // Idk I have to re-populate this thing that's available in the default
            // MutableCreationExtras :/
            val context = LocalContext.current.applicationContext as SuggesterApplication
            set(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY, context)
            set(AppViewModelProvider.CHARACTER_ID_KEY, characterId)
        },
    )
) {
    val coroutineScope = rememberCoroutineScope()
    CharacterEditorWidget(
        modifier = modifier,
        characterProfile = viewModel.characterEditorUiState.characterProfile,
        onSave = { characterProfile ->
            coroutineScope.launch {
                viewModel.saveCharacterProfile(characterProfile)
                showText(
                    navController.context, "Updated ${characterProfile.id}"
                )
            }
        }
    )
}

@Composable
fun CharacterEditorWidget(
    modifier: Modifier,
    characterProfile: CharacterProfile,
    onSave: (CharacterProfile) -> Unit,
) {
    var newDiscordAliases by remember(characterProfile) {
        mutableStateOf(
            getAliasesAsUiString(
                characterProfile,
                ChatWatcherAccessibilityService.DISCORD_PACKAGE
            )
        )
    }
    var newWhatsappAliases by remember(characterProfile) {
        mutableStateOf(
            getAliasesAsUiString(
                characterProfile,
                ChatWatcherAccessibilityService.WHATSAPP_PACKAGE
            )
        )
    }
    var newSenseOfHumor by remember(characterProfile) { mutableStateOf(characterProfile.senseOfHumor) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Editing \"${characterProfile.id}\"",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
        OutlinedTextField(
            value = newDiscordAliases,
            onValueChange = { newDiscordAliases = it },
            label = { Text("Discord aliases (comma-separated)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = newWhatsappAliases,
            onValueChange = { newWhatsappAliases = it },
            label = { Text("Whatsapp aliases (comma-separated)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = newSenseOfHumor,
            onValueChange = { newSenseOfHumor = it },
            label = { Text("Sense of humor") },
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(bottom = 8.dp)
        )
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                newSenseOfHumor = PromptStrings.DEFAULT_SENSE_OF_HUMOR
            }) {
            Text("Use default sense of humor")
        }
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                onSave(
                    CharacterProfile(
                        characterProfile.id,
                        newSenseOfHumor,
                        fromAliasUiToMap(
                            discordAliasUi = newDiscordAliases,
                            whatsappAliasUi = newWhatsappAliases
                        )
                    )
                )
            }) {
            Text("Save")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewCharacterEditorWidget() {
    val mockCharacterProfile = CharacterProfile(
        id = "12345",
        senseOfHumor = "This is a mock character background for preview purposes.",
        aliases = mapOf(
            ChatWatcherAccessibilityService.DISCORD_PACKAGE to listOf("dis1", "dis2"),
            ChatWatcherAccessibilityService.WHATSAPP_PACKAGE to listOf("wa1", "wa2")
        )
    )

    CharacterEditorWidget(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        characterProfile = mockCharacterProfile,
        onSave = { updatedProfile ->
            println("Saving profile: ${updatedProfile.id}")
        }
    )
}
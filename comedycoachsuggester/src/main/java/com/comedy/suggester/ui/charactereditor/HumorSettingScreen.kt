package com.comedy.suggester.ui.charactereditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.comedy.suggester.SuggesterApplication
import com.comedy.suggester.data.CharacterProfile
import com.comedy.suggester.generator.PromptStrings
import com.comedy.suggester.ui.AppViewModelProvider
import com.comedy.suggester.ui.common.showText
import kotlinx.coroutines.launch


private const val LOG_TAG = "HumorSettingScreen"

/**
 * Screen for configuring sense of humor
 */
@Composable
fun HumorSettingScreen(
    modifier: Modifier,
    navController: NavHostController,
    viewModel: CharacterEditorViewModel = viewModel(
        factory = AppViewModelProvider.Factory,
        extras = MutableCreationExtras().apply {
            // Idk I have to re-populate this thing that's available in the default
            // MutableCreationExtras :/
            val context = LocalContext.current.applicationContext as SuggesterApplication
            set(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY, context)
            set(AppViewModelProvider.CHARACTER_ID_KEY, CharacterProfile.MY_ID)
        },
    )
) {
    val coroutineScope = rememberCoroutineScope()
    HumorEditorWidget(
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
fun HumorEditorWidget(
    modifier: Modifier,
    characterProfile: CharacterProfile,
    onSave: (CharacterProfile) -> Unit,
) {
    var newSenseOfHumor by remember(characterProfile) { mutableStateOf(characterProfile.senseOfHumor) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Sense of humor",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
        TextField(
            value = newSenseOfHumor,
            onValueChange = { newSenseOfHumor = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
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
                        characterProfile.aliases
                    )
                )
            }) {
            Text("Save")
        }
    }
}

package com.comedy.suggester.ui.characterselection

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.comedy.suggester.data.CharacterProfile
import com.comedy.suggester.ui.AppViewModelProvider
import com.comedy.suggester.ui.navigateToCharacterEditor
import kotlinx.coroutines.launch


/**
 * Screen for selecting which character profile to edit or create.
 */
@Composable
fun CharacterSelectionScreen(
    navController: NavHostController, modifier: Modifier,
    viewModel: CharacterSelectionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val characterList = viewModel.characterSelectionUiState.characterProfiles.map { it.id }
    val coroutineScope = rememberCoroutineScope()
    CharacterSelectionWidget(
        characterList = characterList,
        onEdit = { selectedCharacter ->
            if (selectedCharacter == NO_CHARACTER_SELECTED) {
                showText(
                    navController.context, "No character selected"
                )
                return@CharacterSelectionWidget
            }
            navigateToCharacterEditor(navController, selectedCharacter)
        },
        onCreate = { newCharacter ->
            coroutineScope.launch {
                viewModel.createNewUser(newCharacter)
                showText(
                    navController.context, "Created: $newCharacter"
                )
                viewModel.reloadCharacterProfiles()
            }
        },
        modifier = modifier
    )
}

fun showText(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

val NO_CHARACTER_SELECTED = "No character selected"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterSelectionWidget(
    modifier: Modifier,
    characterList: List<String>,
    onEdit: (String) -> Unit,
    onCreate: (String) -> Unit
) {
    val selectedCharacter = rememberTextFieldState(NO_CHARACTER_SELECTED)
    var isNameSelectorExpanded by remember { mutableStateOf(false) }
    var newCharacterName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // First Row: Dropdown and Edit existing character
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.weight(1f)) {
                ExposedDropdownMenuBox(
                    expanded = isNameSelectorExpanded,
                    onExpandedChange = { isNameSelectorExpanded = it }
                ) {
                    TextField(
                        // The `menuAnchor` modifier must be passed to the text field to handle
                        // expanding/collapsing the menu on click. A read-only text field has
                        // the anchor type `PrimaryNotEditable`.
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        state = selectedCharacter,
                        readOnly = true,
                        lineLimits = TextFieldLineLimits.SingleLine,
                        label = { Text("Select character to edit") },
                        trailingIcon = { TrailingIcon(expanded = isNameSelectorExpanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = isNameSelectorExpanded,
                        onDismissRequest = { isNameSelectorExpanded = false }
                    ) {
                        characterList.forEach { character ->
                            DropdownMenuItem(
                                text = { Text(character) },
                                onClick = {
                                    selectedCharacter.setTextAndPlaceCursorAtEnd(character)
                                    isNameSelectorExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onEdit(selectedCharacter.text.toString()) }) {
                Text("Edit")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Second Row: TextField and Create new character button
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextField(
                value = newCharacterName,
                onValueChange = { newCharacterName = it },
                placeholder = { Text("New character (${CharacterProfile.MY_ID} for self)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (newCharacterName.isNotBlank()) {
                    onCreate(newCharacterName)
                    newCharacterName = "" // Clear the input field after creation
                }
            }) {
                Text("Create")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CharacterSelectionWidgetPreview() {
    val characterList = listOf("John", "Jane", "Alex")
    CharacterSelectionWidget(
        characterList = characterList,
        onEdit = { selectedCharacter -> println("Editing: $selectedCharacter") },
        onCreate = { newCharacter -> println("Created: $newCharacter") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
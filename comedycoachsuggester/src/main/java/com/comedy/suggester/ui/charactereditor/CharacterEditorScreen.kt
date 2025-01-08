package com.comedy.suggester.ui.charactereditor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

/**
 * Screen for configuring 1 character profile.
 */
@Composable
fun CharacterEditorScreen(
    modifier: Modifier,
    navController: NavHostController,
    characterId: String
) {
    // TODO: Implement
    TextField(
        value = characterId,
        onValueChange = {},
        label = { Text("Test") },
        modifier = modifier.fillMaxWidth()
    )
}
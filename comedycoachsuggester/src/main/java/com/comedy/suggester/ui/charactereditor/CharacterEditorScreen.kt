package com.comedy.suggester.ui.charactereditor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

/**
 * Screen for configuring 1 character profile.
 */
@Composable
fun CharacterEditorScreen(navController: NavController, modifier: Modifier) {
    // TODO: Implement
    TextField(
        value = "Test",
        onValueChange = {},
        label = { Text("Test") },
        modifier = modifier.fillMaxWidth()
    )
}
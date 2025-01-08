package com.comedy.suggester.ui.characterprofile

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

/**
 * Screen for configuring character profiles.
 */
@Composable
fun CharacterProfileScreen(navController: NavController, modifier: Modifier) {
    // TODO: Implement
    TextField(
        value = "Test",
        onValueChange = {},
        label = { Text("Test") },
        modifier = modifier.fillMaxWidth()
    )
}
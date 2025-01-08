package com.comedy.suggester.ui.appsetting

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

/**
 * Screen for configuring app settings like open ai api key
 */
@Composable
fun AppSettingScreen(navController: NavController, modifier: Modifier) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        OpenAiApiKeyTextInput(
            modifier = Modifier.padding(innerPadding)
        )
    }
}
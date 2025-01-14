package com.comedy.suggester.ui.appsetting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.comedy.suggester.ui.AppViewModelProvider
import kotlinx.coroutines.launch

/**
 * Screen for configuring app settings like open ai api key
 */
@Composable
fun AppSettingScreen(
    navController: NavController, modifier: Modifier,
    viewModel: AppSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    AppSettingWidget(
        appSettingsUiState = viewModel.appSettingsUiState,
        onAppSettingsChange = viewModel::updateUiState,
        onSaveClick = {
            coroutineScope.launch {
                viewModel.saveAppSettings()
            }
        },
        modifier = modifier
    )
}

/**
 * Text field and button.
 */
@Composable
fun AppSettingWidget(
    modifier: Modifier = Modifier,
    appSettingsUiState: AppSettingsUiState,
    onAppSettingsChange: (AppSettingsDetails) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = appSettingsUiState.appSettingsDetails.openAiApiKey,
            onValueChange = {
                onAppSettingsChange(
                    appSettingsUiState.appSettingsDetails.copy(openAiApiKey = it)
                )
            },
            label = { Text("Enter OpenAI API Key") },
            modifier = modifier.fillMaxWidth()
        )
        TextField(
            value = appSettingsUiState.appSettingsDetails.anthropicApiKey,
            onValueChange = {
                onAppSettingsChange(
                    appSettingsUiState.appSettingsDetails.copy(anthropicApiKey = it)
                )
            },
            label = { Text("Enter Anthropic API Key") },
            modifier = modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            shape = MaterialTheme.shapes.small,
            modifier = modifier.fillMaxWidth()
        ) {
            Text("Update API key")
        }
    }
}

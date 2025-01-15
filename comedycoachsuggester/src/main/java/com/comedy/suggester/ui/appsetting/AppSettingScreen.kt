package com.comedy.suggester.ui.appsetting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.comedy.suggester.data.LlmModel
import com.comedy.suggester.ui.AppViewModelProvider
import com.comedy.suggester.ui.common.showText
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
                showText(
                    navController.context, "Saved settings"
                )
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
        LlmModelPicker(modifier, appSettingsUiState.appSettingsDetails.llmModel, onValueChange = {
            onAppSettingsChange(
                appSettingsUiState.appSettingsDetails.copy(llmModel = LlmModel.valueOf(it))
            )
        })
        TextField(
            value = appSettingsUiState.appSettingsDetails.openAiApiKey,
            onValueChange = {
                onAppSettingsChange(
                    appSettingsUiState.appSettingsDetails.copy(openAiApiKey = it)
                )
            },
            label = { Text("Enter OpenAI API Key") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        TextField(
            value = appSettingsUiState.appSettingsDetails.anthropicApiKey,
            onValueChange = {
                onAppSettingsChange(
                    appSettingsUiState.appSettingsDetails.copy(anthropicApiKey = it)
                )
            },
            label = { Text("Enter Anthropic API Key") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        Button(
            onClick = onSaveClick,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Update settings")
        }
    }
}

// Drop down menu for llm model picker
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LlmModelPicker(
    modifier: Modifier,
    llmModel: LlmModel,
    onValueChange: (String) -> Unit
) {
    val selectedModel = rememberTextFieldState(llmModel.name)
    var isExpanded by remember { mutableStateOf(false) }

    // Update the TextField state when llmModel changes
    // Without this, rememberTextFieldState won't trigger recomp
    LaunchedEffect(llmModel) {
        selectedModel.setTextAndPlaceCursorAtEnd(llmModel.name)
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .padding(16.dp),
            state = selectedModel,
            readOnly = true,
            lineLimits = TextFieldLineLimits.SingleLine,
            trailingIcon = { TrailingIcon(expanded = isExpanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            LlmModel.entries.forEach { llmModel ->
                DropdownMenuItem(
                    text = { Text(llmModel.name) },
                    onClick = {
                        onValueChange(llmModel.name)
                        selectedModel.setTextAndPlaceCursorAtEnd(llmModel.name)
                        isExpanded = false
                    }
                )
            }
        }
    }
}
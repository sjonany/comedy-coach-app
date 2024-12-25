package com.comedy.controller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.comedy.controller.ui.theme.ComedyCoachControllerTheme
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComedyCoachControllerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    OpenAiApiKeyEntry(Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// Text field for getting open ai api key.
@Composable
fun OpenAiApiKeyEntry(modifier: Modifier = Modifier) {
    // State to hold the text input
    var openAiApiKey by remember { mutableStateOf(TextFieldValue("")) }

    // UI layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = openAiApiKey,
            onValueChange = { openAiApiKey = it },
            label = { Text("Enter OpenAI API Key") },
            singleLine = true,
            modifier = modifier.fillMaxWidth()
        )
        Button(onClick = { /* TODO: Save API key logic here */ }) {
            Text("Update API key")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OpenAiApiKeyEntryPreview() {
    ComedyCoachControllerTheme {
        OpenAiApiKeyEntry(Modifier.padding(10.dp))
    }
}
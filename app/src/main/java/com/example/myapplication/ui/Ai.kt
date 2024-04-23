package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.ai.client.generativeai.GenerativeModel

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val detectedText = intent.getStringExtra("detectedTextKey") ?: "No text detected"
        val apiKey = intent.getStringExtra("API_KEY") ?: "No API Key"
        setContent {

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-pro",
                        apiKey = apiKey
                    )
                    val viewModel = AnswerViewModel(generativeModel)
                    AnswerRoute(detectedText, viewModel)
                }
            }
        }
    }
}

@Composable
internal fun AnswerRoute(
    initialText: String, // Add initialText parameter
    answerViewModel: AnswerViewModel = viewModel()
) {
    val answerUiState by answerViewModel.uiState.collectAsState()

    AnswerScreen(initialText, answerUiState, onAnswerClicked = { inputText ->
        answerViewModel.summarize(inputText)
    })
}

@Composable
fun AnswerScreen(
    initialText: String,
    uiState: AnswerUiState = AnswerUiState.Initial,
    onAnswerClicked: (String) -> Unit = {}
) {
    var prompt by remember { mutableStateOf(initialText) } // Initialize prompt with initialText
    Column(
        modifier = Modifier
            .padding(all = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val buttonHeight = 64.dp // Define the height of the button
        TextField(
            value = prompt,
            label = { Text(stringResource(R.string.summarize_label)) },
            placeholder = { Text(stringResource(R.string.summarize_hint)) },
            onValueChange = { prompt = it },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = buttonHeight)
        )

        TextButton(
            onClick = {
                if (prompt.isNotBlank()) {
                    onAnswerClicked(prompt)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Text(stringResource(R.string.action_go))
        }

        when (uiState) {
            AnswerUiState.Initial -> {
                // Nothing is shown
            }

            AnswerUiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    CircularProgressIndicator()
                }
            }

            is AnswerUiState.Success -> {
                Row(modifier = Modifier.padding(all = 8.dp)) {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = "Person Icon"
                    )
                    Text(
                        text = uiState.outputText,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            is AnswerUiState.Error -> {
                Text(
                    text = uiState.errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(all = 8.dp)
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun AnswerScreenPreview() {
    AnswerScreen(
        initialText = "Sample text", // Provide a sample initialText for preview
        onAnswerClicked = {}
    )
}

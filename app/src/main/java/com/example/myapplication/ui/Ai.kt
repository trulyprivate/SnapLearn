package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.MainScreen
import com.google.ai.client.generativeai.GenerativeModel
import com.example.myapplication.ui.theme.MyApplicationTheme

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val detectedText = intent.getStringExtra("detectedTextKey") ?: "No text detected"
        setContent {

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val generativeModel = GenerativeModel(
                            modelName = "gemini-pro",
                            apiKey = BuildConfig.apiKey
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
        Row {
            TextField(
                value = prompt,
                label = { Text(stringResource(R.string.summarize_label)) },
                placeholder = { Text(stringResource(R.string.summarize_hint)) },
                onValueChange = { prompt = it },
                modifier = Modifier
                    .weight(8f)
            )
            TextButton(
                onClick = {
                    if (prompt.isNotBlank()) {
                        onAnswerClicked(prompt)
                    }
                },

                modifier = Modifier
                    .weight(2f)
                    .padding(all = 4.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(stringResource(R.string.action_go))
            }
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

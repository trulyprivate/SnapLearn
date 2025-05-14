package com.example.myapplication.ui.answer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

/**
 * Composable for the Answer screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnswerScreen(
    initialQuestion: String = "",
    viewModel: AnswerViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var question by remember { mutableStateOf(initialQuestion) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SnapLearn") }
            )
        },
        floatingActionButton = {
            if (uiState is AnswerUiState.Success && !(uiState as AnswerUiState.Success).saved) {
                FloatingActionButton(
                    onClick = { viewModel.saveToHistory() }
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save to history")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                label = { Text("Your question") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { viewModel.generateAnswer(question) },
                enabled = question.isNotBlank() && uiState !is AnswerUiState.Loading,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Generate Answer")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (val state = uiState) {
                    is AnswerUiState.Initial -> {
                        // Initial state, show instructions
                        Text(
                            text = "Enter a question above to get started",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    is AnswerUiState.Loading -> {
                        // Loading state, show spinner
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is AnswerUiState.Success -> {
                        // Success state, show the answer
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "Answer:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = state.text,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            if (state.saved) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Saved to history",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    is AnswerUiState.Error -> {
                        // Error state, show the error message
                        Column(
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text(
                                text = "Error",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
} 
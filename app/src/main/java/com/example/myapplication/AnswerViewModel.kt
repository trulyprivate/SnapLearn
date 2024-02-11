package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnswerViewModel(
        private val generativeModel: GenerativeModel
) : ViewModel() {

    private val _uiState: MutableStateFlow<AnswerUiState> =
            MutableStateFlow(AnswerUiState.Initial)
    val uiState: StateFlow<AnswerUiState> =
            _uiState.asStateFlow()

    fun summarize(inputText: String) {
        if (inputText.isNotBlank()) { // Only proceed if manually typed text is present
            _uiState.value = AnswerUiState.Loading
        }

        val prompt = "Answer these questions of : $inputText in depth"

        viewModelScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                response.text?.let { outputContent ->
                    _uiState.value = AnswerUiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = AnswerUiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}
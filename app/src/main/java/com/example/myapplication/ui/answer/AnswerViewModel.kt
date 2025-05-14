package com.example.myapplication.ui.answer

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaplearn.data.QuestionAnswer
import com.example.snaplearn.viewmodel.AnswerState
import com.example.snaplearn.viewmodel.SharedAnswerViewModel
import com.example.snaplearn.viewmodel.SharedHistoryViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Android-specific ViewModel for the Answer screen that adapts the shared
 * AnswerViewModel for Android UI.
 */
class AnswerViewModel(
    private val sharedAnswerViewModel: SharedAnswerViewModel,
    private val sharedHistoryViewModel: SharedHistoryViewModel
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AnswerUiState>(AnswerUiState.Initial)
    val uiState: StateFlow<AnswerUiState> = _uiState.asStateFlow()
    
    private var lastQuestion: String = ""
    private var lastAnswer: String = ""
    
    init {
        // Observe the shared ViewModel state
        viewModelScope.launch {
            sharedAnswerViewModel.state.collect { state ->
                when (state) {
                    is AnswerState.Initial -> {
                        _uiState.value = AnswerUiState.Initial
                    }
                    is AnswerState.Loading -> {
                        _uiState.value = AnswerUiState.Loading
                    }
                    is AnswerState.Success -> {
                        lastAnswer = state.text
                        _uiState.value = AnswerUiState.Success(state.text)
                    }
                    is AnswerState.Error -> {
                        _uiState.value = AnswerUiState.Error(state.message)
                    }
                }
            }
        }
    }
    
    /**
     * Generates an answer for the given question.
     */
    fun generateAnswer(question: String) {
        lastQuestion = question
        sharedAnswerViewModel.generateAnswer(question)
    }
    
    /**
     * Saves the current question and answer to history.
     */
    fun saveToHistory() {
        if (lastQuestion.isNotBlank() && lastAnswer.isNotBlank()) {
            viewModelScope.launch {
                sharedHistoryViewModel.saveQuestionAnswer(lastQuestion, lastAnswer)
                _uiState.update { 
                    if (it is AnswerUiState.Success) {
                        AnswerUiState.Success(it.text, true)
                    } else {
                        it
                    }
                }
            }
        }
    }
}

/**
 * UI state for the Answer screen.
 */
@Immutable
sealed class AnswerUiState {
    /**
     * Initial state, before any generation has been attempted.
     */
    object Initial : AnswerUiState()
    
    /**
     * Loading state, when generation is in progress.
     */
    object Loading : AnswerUiState()
    
    /**
     * Success state, containing the generated answer.
     */
    data class Success(val text: String, val saved: Boolean = false) : AnswerUiState()
    
    /**
     * Error state, containing the error message.
     */
    data class Error(val message: String) : AnswerUiState()
} 
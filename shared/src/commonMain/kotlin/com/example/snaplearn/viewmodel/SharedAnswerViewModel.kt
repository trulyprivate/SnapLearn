package com.example.snaplearn.viewmodel

import com.example.snaplearn.data.AIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Platform-agnostic ViewModel for handling answer generation.
 * This is shared between Android and iOS.
 */
class SharedAnswerViewModel(
    private val aiService: AIService
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    private val _state = MutableStateFlow<AnswerState>(AnswerState.Initial)
    val state: StateFlow<AnswerState> = _state.asStateFlow()
    
    /**
     * Generate an answer for the given prompt using the AI service.
     */
    fun generateAnswer(prompt: String) {
        if (prompt.isBlank()) {
            _state.value = AnswerState.Error("Question cannot be empty")
            return
        }
        
        _state.value = AnswerState.Loading
        
        scope.launch {
            try {
                // Accumulate the full response as it streams in
                var fullResponse = ""
                
                aiService.generateAnswerStream(prompt)
                    .catch { error -> 
                        _state.value = AnswerState.Error(error.message ?: "Unknown error occurred")
                    }
                    .collect { partialResponse ->
                        fullResponse += partialResponse
                        _state.value = AnswerState.Success(fullResponse)
                    }
            } catch (e: Exception) {
                _state.value = AnswerState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

/**
 * Sealed class representing the different states of the answer generation process.
 */
sealed class AnswerState {
    /**
     * Initial state, before any generation has been attempted.
     */
    object Initial : AnswerState()
    
    /**
     * Loading state, when generation is in progress.
     */
    object Loading : AnswerState()
    
    /**
     * Success state, containing the generated answer.
     */
    data class Success(val text: String) : AnswerState()
    
    /**
     * Error state, containing the error message.
     */
    data class Error(val message: String) : AnswerState()
} 
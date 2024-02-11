package com.example.myapplication

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface AnswerUiState {

    /**
     * Empty state when the screen is first shown
     */
    object Initial : AnswerUiState

    /**
     * Still loading
     */
    object Loading : AnswerUiState

    /**
     * Text has been generated
     */
    data class Success(
            val outputText: String
    ) : AnswerUiState

    /**
     * There was an error generating text
     */
    data class Error(
            val errorMessage: String
    ) : AnswerUiState
}
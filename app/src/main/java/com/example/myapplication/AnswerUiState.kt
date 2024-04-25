package com.example.myapplication

import android.text.SpannedString
import androidx.compose.ui.text.AnnotatedString

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
            val outputText: AnnotatedString
    ) : AnswerUiState

    /**
     * There was an error generating text
     */
    data class Error(
            val errorMessage: AnnotatedString
    ) : AnswerUiState
}
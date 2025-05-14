package com.example.snaplearn.viewmodel

import com.example.snaplearn.data.HistoryRepository
import com.example.snaplearn.data.QuestionAnswer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Platform-agnostic ViewModel for handling history of question-answer pairs.
 * This is shared between Android and iOS.
 */
class SharedHistoryViewModel(
    private val historyRepository: HistoryRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    /**
     * Gets all question-answer pairs as a Flow.
     */
    fun getAllQuestionAnswers(): Flow<List<QuestionAnswer>> {
        return historyRepository.getAllQuestionAnswers()
    }
    
    /**
     * Saves a new question-answer pair.
     */
    fun saveQuestionAnswer(question: String, answer: String) {
        scope.launch {
            historyRepository.saveQuestionAnswer(question, answer)
        }
    }
    
    /**
     * Deletes a question-answer pair by ID.
     */
    fun deleteQuestionAnswer(id: String) {
        scope.launch {
            historyRepository.deleteQuestionAnswer(id)
        }
    }
    
    /**
     * Searches for question-answer pairs matching the query.
     */
    fun searchQuestionAnswers(query: String): Flow<List<QuestionAnswer>> {
        return historyRepository.searchQuestionAnswers(query)
    }
} 
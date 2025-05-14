package com.example.snaplearn.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing and manipulating question-answer history.
 */
interface HistoryRepository {
    /**
     * Gets all question-answer pairs as a Flow.
     * @return A Flow emitting the list of all question-answers, ordered by timestamp (newest first)
     */
    fun getAllQuestionAnswers(): Flow<List<QuestionAnswer>>
    
    /**
     * Gets a specific question-answer pair by ID.
     * @param id The ID of the question-answer to retrieve
     * @return The question-answer pair, or null if not found
     */
    suspend fun getQuestionAnswerById(id: String): QuestionAnswer?
    
    /**
     * Saves a new question-answer pair.
     * @param question The question text
     * @param answer The answer text
     * @return The ID of the newly saved question-answer pair
     */
    suspend fun saveQuestionAnswer(question: String, answer: String): String
    
    /**
     * Deletes a question-answer pair by ID.
     * @param id The ID of the question-answer to delete
     */
    suspend fun deleteQuestionAnswer(id: String)
    
    /**
     * Searches for question-answer pairs matching the query.
     * @param query The search query
     * @return A Flow emitting the list of matching question-answers
     */
    fun searchQuestionAnswers(query: String): Flow<List<QuestionAnswer>>
} 
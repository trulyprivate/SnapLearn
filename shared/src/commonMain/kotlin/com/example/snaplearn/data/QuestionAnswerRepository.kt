package com.example.snaplearn.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing QuestionAnswer data.
 */
interface QuestionAnswerRepository {
    /**
     * Get all question-answer pairs as a Flow.
     */
    fun getAllQuestionAnswers(): Flow<List<QuestionAnswer>>
    
    /**
     * Get a specific question-answer pair by ID.
     */
    suspend fun getQuestionAnswerById(id: Long): QuestionAnswer?
    
    /**
     * Insert a new question-answer pair.
     */
    suspend fun insertQuestionAnswer(question: String, answer: String): Long
    
    /**
     * Delete a question-answer pair by ID.
     */
    suspend fun deleteQuestionAnswer(id: Long): Boolean
    
    /**
     * Delete all question-answer pairs.
     */
    suspend fun deleteAllQuestionAnswers(): Boolean
} 
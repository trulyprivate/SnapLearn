package com.example.snaplearn.data

import kotlinx.coroutines.flow.Flow

/**
 * Interface for AI services that generate answers from text prompts.
 */
interface AIService {
    /**
     * Generates an answer for the given prompt synchronously.
     * @param prompt The text prompt to generate an answer for
     * @return The generated answer
     */
    suspend fun generateAnswer(prompt: String): String
    
    /**
     * Generates an answer for the given prompt as a stream of text chunks.
     * @param prompt The text prompt to generate an answer for
     * @return A flow of text chunks that make up the complete answer
     */
    fun generateAnswerStream(prompt: String): Flow<String>
} 
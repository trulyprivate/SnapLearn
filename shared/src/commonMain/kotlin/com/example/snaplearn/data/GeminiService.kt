package com.example.snaplearn.data

import kotlinx.coroutines.flow.Flow

/**
 * Implementation of AIService that uses Google's Gemini Pro model.
 */
class GeminiService(
    private val geminiApiClient: GeminiApiClient
) : AIService {
    /**
     * Generates an answer for the given prompt synchronously.
     */
    override suspend fun generateAnswer(prompt: String): String {
        return geminiApiClient.generateContent(prompt)
    }
    
    /**
     * Generates an answer for the given prompt as a stream of text chunks.
     */
    override fun generateAnswerStream(prompt: String): Flow<String> {
        return geminiApiClient.generateContentStream(prompt)
    }
} 
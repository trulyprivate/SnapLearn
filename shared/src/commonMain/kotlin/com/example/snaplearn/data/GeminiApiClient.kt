package com.example.snaplearn.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

/**
 * Client for interacting with Google's Gemini Pro AI model.
 * Provides methods to send prompts and receive responses.
 */
class GeminiApiClient(private val apiKeyProvider: ApiKeyProvider) {
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"
    
    // Create HTTP client with JSON serialization
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
            })
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }
    
    /**
     * Send a prompt to Gemini Pro and get the response as a flow of text chunks.
     * This allows streaming the response as it's generated.
     *
     * @param prompt The text prompt to send to the model
     * @param temperature Sampling temperature (0.0 to 1.0), higher means more creative
     * @return Flow of response text chunks
     */
    suspend fun generateContentStream(prompt: String, temperature: Float = 0.7f): Flow<String> = flow {
        try {
            val apiKey = apiKeyProvider.getApiKey()
            val url = "$baseUrl?key=$apiKey"
            
            val requestBody = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = prompt)
                        )
                    )
                ),
                generationConfig = GenerationConfig(
                    temperature = temperature,
                    maxOutputTokens = 2048
                )
            )
            
            val response: GeminiResponse = httpClient.post(url) {
                setBody(requestBody)
            }.body()
            
            // Process and emit response parts
            response.candidates.forEach { candidate ->
                candidate.content.parts.forEach { part ->
                    part.text?.let { text ->
                        emit(text)
                    }
                }
            }
        } catch (e: Exception) {
            throw GeminiApiException("Failed to generate content", e)
        }
    }
    
    /**
     * Send a prompt to Gemini Pro and get the full response text.
     *
     * @param prompt The text prompt to send to the model
     * @param temperature Sampling temperature (0.0 to 1.0), higher means more creative
     * @return Complete response text
     */
    suspend fun generateContent(prompt: String, temperature: Float = 0.7f): String {
        var fullResponse = ""
        generateContentStream(prompt, temperature).collect { chunk ->
            fullResponse += chunk
        }
        return fullResponse
    }
    
    /**
     * Close the HTTP client and release resources.
     */
    fun close() {
        httpClient.close()
    }
}

/**
 * Exception thrown when there's an error interacting with the Gemini API.
 */
class GeminiApiException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Interface for providing the API key securely.
 */
interface ApiKeyProvider {
    /**
     * Get the Gemini API key.
     */
    suspend fun getApiKey(): String
} 
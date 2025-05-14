package com.example.snaplearn.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json

class GeminiApiClientTest {
    
    private val mockApiKeyProvider = object : ApiKeyProvider {
        override suspend fun getApiKey(): String = "test-api-key"
    }
    
    private fun createMockHttpClient(responseContent: String, statusCode: HttpStatusCode = HttpStatusCode.OK) = HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                // Verify request parameters
                val url = request.url.toString()
                val containsApiKey = url.contains("key=test-api-key")
                val isCorrectEndpoint = url.contains("gemini-pro:generateContent") || url.contains("gemini-pro:streamGenerateContent")
                
                if (containsApiKey && isCorrectEndpoint) {
                    respond(
                        content = responseContent,
                        status = statusCode,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                } else {
                    respond(
                        content = """{"error": "Invalid request parameters"}""",
                        status = HttpStatusCode.BadRequest,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                }
            }
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
    
    @Test
    fun testGenerateContent_success() = runTest {
        // Given
        val successResponse = """
        {
            "candidates": [
                {
                    "content": {
                        "parts": [
                            {
                                "text": "This is a test response from Gemini."
                            }
                        ]
                    },
                    "finishReason": "STOP"
                }
            ]
        }
        """
        
        val httpClient = createMockHttpClient(successResponse)
        val geminiApiClient = GeminiApiClient(mockApiKeyProvider)
        
        // When
        val result = geminiApiClient.generateContent("What is the capital of France?")
        
        // Then
        assertEquals("This is a test response from Gemini.", result)
    }
    
    @Test
    fun testGenerateContentStream_success() = runTest {
        // Given
        val successResponse = """
        {
            "candidates": [
                {
                    "content": {
                        "parts": [
                            {
                                "text": "This is a streaming response"
                            }
                        ]
                    },
                    "finishReason": null
                }
            ]
        }
        """
        
        val httpClient = createMockHttpClient(successResponse)
        val geminiApiClient = GeminiApiClient(mockApiKeyProvider)
        
        // When
        val flow = geminiApiClient.generateContentStream("What is the capital of France?")
        
        // Then
        val firstChunk = flow.first()
        assertEquals("This is a streaming response", firstChunk)
    }
    
    @Test
    fun testGenerateContent_error() = runTest {
        // Given
        val errorResponse = """
        {
            "error": {
                "code": 400,
                "message": "Invalid request",
                "status": "INVALID_ARGUMENT"
            }
        }
        """
        
        val httpClient = createMockHttpClient(errorResponse, HttpStatusCode.BadRequest)
        val geminiApiClient = GeminiApiClient(mockApiKeyProvider)
        
        // When & Then
        assertFailsWith<GeminiApiException> {
            geminiApiClient.generateContent("Invalid prompt")
        }
    }
    
    @Test
    fun testApiKeyNotAvailable() = runTest {
        // Given
        val emptyApiKeyProvider = object : ApiKeyProvider {
            override suspend fun getApiKey(): String = throw IllegalStateException("API key not found")
        }
        
        val httpClient = createMockHttpClient("{}")
        val geminiApiClient = GeminiApiClient(emptyApiKeyProvider)
        
        // When & Then
        assertFailsWith<GeminiApiException> {
            geminiApiClient.generateContent("Test prompt")
        }
    }
} 
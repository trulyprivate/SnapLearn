package com.example.snaplearn.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for Gemini API.
 */
@Serializable
data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val safetySettings: List<SafetySetting>? = null
)

/**
 * Content object containing parts of the request.
 */
@Serializable
data class Content(
    val parts: List<Part>,
    val role: String? = "user"
)

/**
 * Part of the request content, typically text.
 */
@Serializable
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

/**
 * Inline data for sending images or other binary content.
 */
@Serializable
data class InlineData(
    val mimeType: String,
    val data: String // Base64 encoded data
)

/**
 * Configuration for the generation process.
 */
@Serializable
data class GenerationConfig(
    val temperature: Float? = null,
    val topK: Int? = null,
    val topP: Float? = null,
    val maxOutputTokens: Int? = null,
    val stopSequences: List<String>? = null
)

/**
 * Setting for the safety filters.
 */
@Serializable
data class SafetySetting(
    val category: String,
    val threshold: String
)

/**
 * Response model from Gemini API.
 */
@Serializable
data class GeminiResponse(
    val candidates: List<Candidate> = emptyList(),
    val promptFeedback: PromptFeedback? = null
)

/**
 * Candidate representing a generated response.
 */
@Serializable
data class Candidate(
    val content: Content,
    val finishReason: String? = null,
    val safetyRatings: List<SafetyRating>? = null,
    val index: Int? = null
)

/**
 * Feedback about the prompt.
 */
@Serializable
data class PromptFeedback(
    val safetyRatings: List<SafetyRating>? = null
)

/**
 * Safety rating for content.
 */
@Serializable
data class SafetyRating(
    val category: String,
    val probability: String
) 
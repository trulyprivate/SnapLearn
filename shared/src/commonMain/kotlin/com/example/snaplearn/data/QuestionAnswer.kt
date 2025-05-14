package com.example.snaplearn.data

import kotlinx.serialization.Serializable

/**
 * Data model representing a question-answer pair.
 */
@Serializable
data class QuestionAnswer(
    /** Unique identifier for this question-answer pair */
    val id: String,
    
    /** The question text */
    val question: String,
    
    /** The answer text */
    val answer: String,
    
    /** Timestamp when this pair was created (milliseconds since epoch) */
    val createdAt: Long,
    
    /** Whether this question-answer pair is favorited */
    val favorited: Boolean = false,
    
    /** Optional image data associated with this question */
    val imageData: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as QuestionAnswer

        if (id != other.id) return false
        if (question != other.question) return false
        if (answer != other.answer) return false
        if (createdAt != other.createdAt) return false
        if (favorited != other.favorited) return false
        if (imageData != null) {
            if (other.imageData == null) return false
            if (!imageData.contentEquals(other.imageData)) return false
        } else if (other.imageData != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + question.hashCode()
        result = 31 * result + answer.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + favorited.hashCode()
        result = 31 * result + (imageData?.contentHashCode() ?: 0)
        return result
    }
} 
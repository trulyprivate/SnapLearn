package com.example.snaplearn.data

/**
 * Represents a question and its answer in the application.
 *
 * @property id The unique identifier for this question-answer pair.
 * @property question The question text.
 * @property answer The answer text.
 * @property dateTime The timestamp when this entry was created (in milliseconds since epoch).
 */
data class QuestionAnswer(
    val id: Long = 0,
    val question: String,
    val answer: String,
    val dateTime: Long
) 
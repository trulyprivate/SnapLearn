package com.example.snaplearn.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.snaplearn.database.QuestionAnswer
import com.example.snaplearn.database.SnapLearnDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

/**
 * Repository for managing QuestionAnswer data in the database.
 */
class QuestionAnswerRepository(private val database: SnapLearnDatabase) {
    
    /**
     * Gets all question/answer pairs as a flow.
     */
    fun getAllQuestionAnswersAsFlow(): Flow<List<QuestionAnswer>> {
        return database.questionAnswerQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }
    
    /**
     * Gets all question/answer pairs.
     */
    suspend fun getAllQuestionAnswers(): List<QuestionAnswer> = withContext(Dispatchers.IO) {
        database.questionAnswerQueries.selectAll().executeAsList()
    }
    
    /**
     * Gets a specific question/answer pair by ID.
     */
    suspend fun getQuestionAnswerById(id: String): QuestionAnswer? = withContext(Dispatchers.IO) {
        database.questionAnswerQueries.getById(id).executeAsOneOrNull()
    }
    
    /**
     * Gets all favorited question/answer pairs.
     */
    suspend fun getFavorites(): List<QuestionAnswer> = withContext(Dispatchers.IO) {
        database.questionAnswerQueries.getFavorites().executeAsList()
    }
    
    /**
     * Gets all favorited question/answer pairs as a flow.
     */
    fun getFavoritesAsFlow(): Flow<List<QuestionAnswer>> {
        return database.questionAnswerQueries.getFavorites()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }
    
    /**
     * Searches for question/answer pairs that match the query.
     */
    suspend fun search(query: String): List<QuestionAnswer> = withContext(Dispatchers.IO) {
        database.questionAnswerQueries.searchQuestionAnswers(query).executeAsList()
    }
    
    /**
     * Adds a new question/answer pair.
     */
    suspend fun addQuestionAnswer(
        question: String,
        answer: String,
        imageData: ByteArray? = null
    ): String = withContext(Dispatchers.IO) {
        val id = UUID.generateUUID().toString()
        val createdAt = Clock.System.now().toEpochMilliseconds()
        
        database.questionAnswerQueries.insert(
            id = id,
            question = question,
            answer = answer,
            imageData = imageData,
            createdAt = createdAt,
            favorited = false
        )
        
        return@withContext id
    }
    
    /**
     * Updates an existing question/answer pair.
     */
    suspend fun updateQuestionAnswer(
        id: String,
        question: String,
        answer: String,
        imageData: ByteArray? = null,
        favorited: Boolean
    ) = withContext(Dispatchers.IO) {
        database.questionAnswerQueries.update(
            question = question,
            answer = answer,
            imageData = imageData,
            favorited = favorited,
            id = id
        )
    }
    
    /**
     * Deletes a question/answer pair.
     */
    suspend fun deleteQuestionAnswer(id: String) = withContext(Dispatchers.IO) {
        database.questionAnswerQueries.delete(id)
    }
    
    /**
     * Toggles the favorite status of a question/answer pair.
     */
    suspend fun toggleFavorite(id: String) = withContext(Dispatchers.IO) {
        database.questionAnswerQueries.toggleFavorite(id)
    }
} 
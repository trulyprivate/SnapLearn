package com.example.snaplearn.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.snaplearn.shared.database.SnapLearnDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

/**
 * Implementation of HistoryRepository that uses SQLDelight for data storage.
 */
class SQLDelightHistoryRepository(
    private val databaseDriverFactory: DatabaseDriverFactory
) : HistoryRepository {
    private val database = SnapLearnDatabase(databaseDriverFactory.createDriver())
    private val dbQueries = database.questionAnswerQueries
    
    /**
     * Gets all question-answer pairs as a Flow.
     */
    override fun getAllQuestionAnswers(): Flow<List<QuestionAnswer>> {
        return dbQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { dbItems ->
                dbItems.map { it.toQuestionAnswer() }
            }
    }
    
    /**
     * Gets a specific question-answer pair by ID.
     */
    override suspend fun getQuestionAnswerById(id: String): QuestionAnswer? {
        return dbQueries.getById(id)
            .executeAsOneOrNull()
            ?.toQuestionAnswer()
    }
    
    /**
     * Saves a new question-answer pair.
     */
    override suspend fun saveQuestionAnswer(question: String, answer: String): String {
        val id = UUID.generateUUID().toString()
        val timestamp = Clock.System.now().toEpochMilliseconds()
        
        dbQueries.insert(
            id = id,
            question = question,
            answer = answer,
            imageData = null,
            createdAt = timestamp,
            favorited = false
        )
        
        return id
    }
    
    /**
     * Deletes a question-answer pair by ID.
     */
    override suspend fun deleteQuestionAnswer(id: String) {
        dbQueries.delete(id)
    }
    
    /**
     * Searches for question-answer pairs matching the query.
     */
    override fun searchQuestionAnswers(query: String): Flow<List<QuestionAnswer>> {
        return dbQueries.searchQuestionAnswers("%$query%", "%$query%")
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { dbItems ->
                dbItems.map { it.toQuestionAnswer() }
            }
    }
    
    /**
     * Converts a database entity to a QuestionAnswer model.
     */
    private fun com.example.snaplearn.database.QuestionAnswer.toQuestionAnswer(): QuestionAnswer {
        return QuestionAnswer(
            id = id,
            question = question,
            answer = answer,
            createdAt = createdAt,
            favorited = favorited,
            imageData = imageData
        )
    }
} 
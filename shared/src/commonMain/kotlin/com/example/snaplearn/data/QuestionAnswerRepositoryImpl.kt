package com.example.snaplearn.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.snaplearn.shared.database.SnapLearnDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class QuestionAnswerRepositoryImpl(
    private val database: SnapLearnDatabase
) : QuestionAnswerRepository {

    private val queries = database.questionAnswersQueries
    
    override fun getAllQuestionAnswers(): Flow<List<QuestionAnswer>> {
        return queries.getAllQuestionAnswers()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows ->
                rows.map { row ->
                    QuestionAnswer(
                        id = row.id,
                        question = row.question,
                        answer = row.answer,
                        dateTime = row.dateTime
                    )
                }
            }
    }
    
    override suspend fun getQuestionAnswerById(id: Long): QuestionAnswer? {
        return withContext(Dispatchers.Default) {
            queries.getQuestionAnswerById(id).executeAsOneOrNull()?.let { row ->
                QuestionAnswer(
                    id = row.id,
                    question = row.question,
                    answer = row.answer,
                    dateTime = row.dateTime
                )
            }
        }
    }
    
    override suspend fun insertQuestionAnswer(question: String, answer: String): Long {
        return withContext(Dispatchers.Default) {
            val currentTime = Clock.System.now().toEpochMilliseconds()
            queries.insertQuestionAnswer(question, answer, currentTime)
            // Get the last inserted row ID
            queries.getAllQuestionAnswers().executeAsList().firstOrNull()?.id ?: 0L
        }
    }
    
    override suspend fun deleteQuestionAnswer(id: Long): Boolean {
        return withContext(Dispatchers.Default) {
            queries.deleteQuestionAnswer(id)
            true
        }
    }
    
    override suspend fun deleteAllQuestionAnswers(): Boolean {
        return withContext(Dispatchers.Default) {
            queries.deleteAllQuestionAnswers()
            true
        }
    }
} 
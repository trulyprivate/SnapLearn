package com.example.snaplearn.data

import com.example.snaplearn.database.SnapLearnDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Manager class for the SnapLearn database.
 * Handles initializing the database and provides access to repositories.
 */
class DatabaseManager(databaseDriverFactory: DatabaseDriverFactory) {
    
    // Initialize the database
    private val database = SnapLearnDatabase(databaseDriverFactory.createDriver())
    
    // Initialize repositories
    val questionAnswerRepository = QuestionAnswerRepository(database)
    
    /**
     * Deletes all data from the database.
     */
    suspend fun clearDatabase() = withContext(Dispatchers.IO) {
        // Add more tables as needed
        database.questionAnswerQueries.transaction {
            database.questionAnswerQueries.selectAll().executeAsList().forEach { qa ->
                database.questionAnswerQueries.delete(qa.id)
            }
        }
    }
    
    /**
     * Closes the database connection.
     */
    fun closeDatabase() {
        database.questionAnswerQueries.database.close()
    }
} 
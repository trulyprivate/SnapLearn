package com.example.snaplearn.data

import app.cash.sqldelight.db.SqlDriver
import com.example.snaplearn.database.QuestionAnswer
import com.example.snaplearn.database.SnapLearnDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for [QuestionAnswerRepository]
 */
class QuestionAnswerRepositoryTest {
    // Test dependencies
    private lateinit var driver: SqlDriver
    private lateinit var database: SnapLearnDatabase
    private lateinit var repository: QuestionAnswerRepository

    // Test data
    private val testQuestion = "What is Kotlin Multiplatform?"
    private val testAnswer = "Kotlin Multiplatform is a technology that allows sharing code between different platforms."
    
    /**
     * Set up the test environment before each test.
     * Creates an in-memory database and initializes the repository.
     */
    @BeforeTest
    fun setup() {
        driver = createInMemorySqlDriver()
        database = SnapLearnDatabase(driver)
        repository = QuestionAnswerRepository(database)
    }
    
    /**
     * Clean up after each test.
     * Closes the database connection.
     */
    @AfterTest
    fun tearDown() {
        driver.close()
    }
    
    /**
     * Test adding and retrieving a question-answer pair.
     */
    @Test
    fun testAddAndGetQuestionAnswer() = runTest {
        // Add a new question-answer pair
        val id = repository.addQuestionAnswer(testQuestion, testAnswer)
        
        // Verify it was added correctly
        val result = repository.getQuestionAnswerById(id)
        
        // Assertions
        assertNotNull(result)
        assertEquals(testQuestion, result.question)
        assertEquals(testAnswer, result.answer)
        assertEquals(false, result.favorited)
    }
    
    /**
     * Test getting all question-answer pairs as a flow.
     */
    @Test
    fun testGetAllQuestionAnswersAsFlow() = runTest {
        // Add some test data
        repository.addQuestionAnswer("Question 1", "Answer 1")
        repository.addQuestionAnswer("Question 2", "Answer 2")
        repository.addQuestionAnswer("Question 3", "Answer 3")
        
        // Get the flow and collect the first emission
        val results = repository.getAllQuestionAnswersAsFlow().first()
        
        // Assertions
        assertEquals(3, results.size)
        // Verify they're sorted by createdAt DESC (most recent first)
        assertTrue(results[0].createdAt >= results[1].createdAt)
        assertTrue(results[1].createdAt >= results[2].createdAt)
    }
    
    /**
     * Test updating a question-answer pair.
     */
    @Test
    fun testUpdateQuestionAnswer() = runTest {
        // Add a new question-answer pair
        val id = repository.addQuestionAnswer(testQuestion, testAnswer)
        
        // Update it
        val updatedQuestion = "Updated Question"
        val updatedAnswer = "Updated Answer"
        val favorited = true
        repository.updateQuestionAnswer(id, updatedQuestion, updatedAnswer, null, favorited)
        
        // Get the updated version
        val result = repository.getQuestionAnswerById(id)
        
        // Assertions
        assertNotNull(result)
        assertEquals(updatedQuestion, result.question)
        assertEquals(updatedAnswer, result.answer)
        assertEquals(favorited, result.favorited)
    }
    
    /**
     * Test deleting a question-answer pair.
     */
    @Test
    fun testDeleteQuestionAnswer() = runTest {
        // Add a new question-answer pair
        val id = repository.addQuestionAnswer(testQuestion, testAnswer)
        
        // Verify it exists
        assertNotNull(repository.getQuestionAnswerById(id))
        
        // Delete it
        repository.deleteQuestionAnswer(id)
        
        // Verify it no longer exists
        assertNull(repository.getQuestionAnswerById(id))
    }
    
    /**
     * Test toggling the favorite status of a question-answer pair.
     */
    @Test
    fun testToggleFavorite() = runTest {
        // Add a new question-answer pair (default favorited = false)
        val id = repository.addQuestionAnswer(testQuestion, testAnswer)
        
        // Toggle favorite status
        repository.toggleFavorite(id)
        
        // Verify it's now favorited
        val result1 = repository.getQuestionAnswerById(id)
        assertNotNull(result1)
        assertEquals(true, result1.favorited)
        
        // Toggle favorite status again
        repository.toggleFavorite(id)
        
        // Verify it's now unfavorited
        val result2 = repository.getQuestionAnswerById(id)
        assertNotNull(result2)
        assertEquals(false, result2.favorited)
    }
    
    /**
     * Test getting favorite question-answer pairs.
     */
    @Test
    fun testGetFavorites() = runTest {
        // Add some test data with varying favorite status
        val id1 = repository.addQuestionAnswer("Question 1", "Answer 1")
        val id2 = repository.addQuestionAnswer("Question 2", "Answer 2")
        val id3 = repository.addQuestionAnswer("Question 3", "Answer 3")
        
        // Make some favorites
        repository.toggleFavorite(id1)
        repository.toggleFavorite(id3)
        
        // Get favorites
        val favorites = repository.getFavorites()
        
        // Assertions
        assertEquals(2, favorites.size)
        assertEquals(setOf(id1, id3), favorites.map { it.id }.toSet())
    }
}

/**
 * Creates an in-memory SQL driver for testing.
 * This function should be implemented according to the platform-specific test requirements.
 */
expect fun createInMemorySqlDriver(): SqlDriver 
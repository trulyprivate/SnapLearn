package com.example.snaplearn.viewmodel

import com.example.snaplearn.data.HistoryRepository
import com.example.snaplearn.data.QuestionAnswer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for [SharedHistoryViewModel]
 */
class SharedHistoryViewModelTest {

    // Test dependencies
    private lateinit var mockRepository: MockHistoryRepository
    private lateinit var viewModel: SharedHistoryViewModel
    
    // Test data
    private val testQuestion1 = "What is Kotlin?"
    private val testAnswer1 = "Kotlin is a modern programming language."
    private val testQuestion2 = "What is KMM?"
    private val testAnswer2 = "Kotlin Multiplatform Mobile (KMM) allows sharing code between mobile platforms."
    
    @BeforeTest
    fun setup() {
        mockRepository = MockHistoryRepository()
        viewModel = SharedHistoryViewModel(mockRepository)
    }
    
    @Test
    fun `getAllQuestionAnswers returns flow from repository`() = runTest {
        // Setup test data
        val testData = listOf(
            createQuestionAnswer("1", testQuestion1, testAnswer1),
            createQuestionAnswer("2", testQuestion2, testAnswer2)
        )
        mockRepository.historyItems.value = testData
        
        // Call method
        val result = viewModel.getAllQuestionAnswers().first()
        
        // Check results
        assertEquals(2, result.size)
        assertEquals(testData, result)
    }
    
    @Test
    fun `saveQuestionAnswer calls repository`() = runTest {
        // Call method
        viewModel.saveQuestionAnswer(testQuestion1, testAnswer1)
        
        // Check repository was called with correct arguments
        assertTrue(mockRepository.saveWasCalled)
        assertEquals(testQuestion1, mockRepository.lastSavedQuestion)
        assertEquals(testAnswer1, mockRepository.lastSavedAnswer)
    }
    
    @Test
    fun `deleteQuestionAnswer calls repository`() = runTest {
        // Setup
        val testId = "test-id"
        
        // Call method
        viewModel.deleteQuestionAnswer(testId)
        
        // Check repository was called with correct argument
        assertTrue(mockRepository.deleteWasCalled)
        assertEquals(testId, mockRepository.lastDeletedId)
    }
    
    @Test
    fun `searchQuestionAnswers returns flow from repository`() = runTest {
        // Setup test data
        val query = "Kotlin"
        val testData = listOf(
            createQuestionAnswer("1", testQuestion1, testAnswer1)
        )
        mockRepository.searchResults = testData
        
        // Call method
        val result = viewModel.searchQuestionAnswers(query).first()
        
        // Check results
        assertEquals(1, result.size)
        assertEquals(testData, result)
        assertEquals(query, mockRepository.lastSearchQuery)
    }
    
    // Helper function to create test QuestionAnswer objects
    private fun createQuestionAnswer(id: String, question: String, answer: String): QuestionAnswer {
        return QuestionAnswer(
            id = id,
            question = question,
            answer = answer,
            createdAt = System.currentTimeMillis(),
            favorited = false
        )
    }
}

/**
 * Mock HistoryRepository implementation for testing.
 */
class MockHistoryRepository : HistoryRepository {
    // Observable state for test data
    val historyItems = MutableStateFlow<List<QuestionAnswer>>(emptyList())
    var searchResults: List<QuestionAnswer> = emptyList()
    
    // Tracking method calls
    var saveWasCalled = false
    var deleteWasCalled = false
    var lastSavedQuestion = ""
    var lastSavedAnswer = ""
    var lastDeletedId = ""
    var lastSearchQuery = ""
    
    override fun getAllQuestionAnswers(): Flow<List<QuestionAnswer>> {
        return historyItems
    }
    
    override suspend fun getQuestionAnswerById(id: String): QuestionAnswer? {
        return historyItems.value.find { it.id == id }
    }
    
    override suspend fun saveQuestionAnswer(question: String, answer: String): String {
        saveWasCalled = true
        lastSavedQuestion = question
        lastSavedAnswer = answer
        return "mock-id"
    }
    
    override suspend fun deleteQuestionAnswer(id: String) {
        deleteWasCalled = true
        lastDeletedId = id
    }
    
    override fun searchQuestionAnswers(query: String): Flow<List<QuestionAnswer>> {
        lastSearchQuery = query
        return flow { emit(searchResults) }
    }
} 
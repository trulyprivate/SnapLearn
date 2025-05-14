package com.example.snaplearn.integration

import com.example.snaplearn.data.HistoryRepository
import com.example.snaplearn.data.QuestionAnswer
import com.example.snaplearn.viewmodel.MockAIService
import com.example.snaplearn.viewmodel.MockHistoryRepository
import com.example.snaplearn.viewmodel.SharedAnswerViewModel
import com.example.snaplearn.viewmodel.SharedHistoryViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration tests for the interaction between ViewModels and Repository.
 */
class ViewModelRepositoryIntegrationTest {
    
    // Test dependencies
    private lateinit var mockAIService: MockAIService
    private lateinit var mockHistoryRepository: MockHistoryRepository
    private lateinit var answerViewModel: SharedAnswerViewModel
    private lateinit var historyViewModel: SharedHistoryViewModel
    
    @BeforeTest
    fun setup() {
        mockAIService = MockAIService()
        mockHistoryRepository = MockHistoryRepository()
        answerViewModel = SharedAnswerViewModel(mockAIService)
        historyViewModel = SharedHistoryViewModel(mockHistoryRepository)
    }
    
    /**
     * Tests that a question-answer pair generated in the AnswerViewModel
     * can be saved and retrieved through the HistoryViewModel.
     */
    @Test
    fun `answer generation and saving workflow`() = runTest {
        // 1. Generate an answer using the AnswerViewModel
        val testQuestion = "What is KMM?"
        val testAnswer = "Kotlin Multiplatform Mobile (KMM) is a cross-platform framework."
        mockAIService.nextResponse = testAnswer
        
        answerViewModel.generateAnswer(testQuestion)
        advanceUntilIdle() // Wait for async operations
        
        // 2. Save the question and answer using the HistoryViewModel
        historyViewModel.saveQuestionAnswer(testQuestion, testAnswer)
        advanceUntilIdle() // Wait for async operations
        
        // 3. Verify the repository was called with the correct data
        assertTrue(mockHistoryRepository.saveWasCalled)
        assertEquals(testQuestion, mockHistoryRepository.lastSavedQuestion)
        assertEquals(testAnswer, mockHistoryRepository.lastSavedAnswer)
        
        // 4. Add the saved item to the mock repository's state to simulate saving
        val savedItem = QuestionAnswer(
            id = "mock-id",
            question = testQuestion,
            answer = testAnswer,
            createdAt = System.currentTimeMillis(),
            favorited = false
        )
        mockHistoryRepository.historyItems.value = listOf(savedItem)
        
        // 5. Get all items using the HistoryViewModel and verify the saved item is returned
        val historyItems = historyViewModel.getAllQuestionAnswers().first()
        
        assertEquals(1, historyItems.size)
        assertEquals(testQuestion, historyItems[0].question)
        assertEquals(testAnswer, historyItems[0].answer)
    }
    
    /**
     * Tests search functionality integration between ViewModel and Repository.
     */
    @Test
    fun `search workflow`() = runTest {
        // 1. Set up test data in the repository
        val items = listOf(
            QuestionAnswer(
                id = "1", 
                question = "What is Kotlin?", 
                answer = "A modern programming language.",
                createdAt = System.currentTimeMillis() - 1000,
                favorited = false
            ),
            QuestionAnswer(
                id = "2", 
                question = "What is KMM?", 
                answer = "Kotlin Multiplatform Mobile framework.",
                createdAt = System.currentTimeMillis(),
                favorited = true
            )
        )
        
        // 2. Set up mocked search results
        val searchQuery = "Kotlin"
        mockHistoryRepository.searchResults = listOf(items[0])
        
        // 3. Perform search using the ViewModel
        val searchResults = historyViewModel.searchQuestionAnswers(searchQuery).first()
        
        // 4. Verify correct search query was passed to repository
        assertEquals(searchQuery, mockHistoryRepository.lastSearchQuery)
        
        // 5. Verify search results
        assertEquals(1, searchResults.size)
        assertEquals("What is Kotlin?", searchResults[0].question)
    }
    
    /**
     * Tests deletion integration between ViewModel and Repository.
     */
    @Test
    fun `deletion workflow`() = runTest {
        // 1. Set up initial data
        val itemId = "test-id"
        val testItem = QuestionAnswer(
            id = itemId,
            question = "Test Question",
            answer = "Test Answer",
            createdAt = System.currentTimeMillis(),
            favorited = false
        )
        mockHistoryRepository.historyItems.value = listOf(testItem)
        
        // 2. Verify the item exists in the repository
        val initialItems = historyViewModel.getAllQuestionAnswers().first()
        assertEquals(1, initialItems.size)
        
        // 3. Delete the item using the ViewModel
        historyViewModel.deleteQuestionAnswer(itemId)
        advanceUntilIdle() // Wait for async operations
        
        // 4. Verify deletion request was sent to repository
        assertTrue(mockHistoryRepository.deleteWasCalled)
        assertEquals(itemId, mockHistoryRepository.lastDeletedId)
        
        // 5. Simulate the deletion in the mock repository
        mockHistoryRepository.historyItems.value = emptyList()
        
        // 6. Verify the item is no longer returned
        val updatedItems = historyViewModel.getAllQuestionAnswers().first()
        assertEquals(0, updatedItems.size)
    }
} 
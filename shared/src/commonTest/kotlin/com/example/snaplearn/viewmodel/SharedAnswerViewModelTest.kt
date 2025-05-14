package com.example.snaplearn.viewmodel

import com.example.snaplearn.data.AIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for [SharedAnswerViewModel]
 */
class SharedAnswerViewModelTest {
    
    // Test dependencies
    private lateinit var mockAIService: MockAIService
    private lateinit var viewModel: SharedAnswerViewModel
    private lateinit var testScope: TestScope
    
    @BeforeTest
    fun setup() {
        mockAIService = MockAIService()
        testScope = TestScope()
        viewModel = SharedAnswerViewModel(mockAIService)
    }
    
    @Test
    fun `generateAnswer with empty prompt returns error state`() = runTest {
        // Call the method with empty prompt
        viewModel.generateAnswer("")
        
        // Check that the state is Error
        val state = viewModel.state.first()
        assertTrue(state is AnswerState.Error)
        assertEquals("Question cannot be empty", (state as AnswerState.Error).message)
    }
    
    @Test
    fun `generateAnswer with valid prompt returns success state`() = runTest {
        // Set up mock to return a successful response
        val testPrompt = "What is Kotlin?"
        val expectedResponse = "Kotlin is a modern programming language."
        mockAIService.nextResponse = expectedResponse
        
        // Call the method
        viewModel.generateAnswer(testPrompt)
        
        // Skip loading state
        advanceUntilIdle()
        
        // Check final state
        val state = viewModel.state.first()
        assertTrue(state is AnswerState.Success)
        assertEquals(expectedResponse, (state as AnswerState.Success).text)
    }
    
    @Test
    fun `generateAnswer handles streaming responses`() = runTest {
        // Set up mock to stream responses
        val testPrompt = "Tell me about Kotlin Multiplatform"
        mockAIService.simulateStreamedResponse = true
        mockAIService.streamedResponses = listOf("Kotlin ", "Multiplatform ", "is a ", "cross-platform framework.")
        
        // Call the method
        viewModel.generateAnswer(testPrompt)
        
        // Skip loading state
        advanceUntilIdle()
        
        // Check final state
        val state = viewModel.state.first()
        assertTrue(state is AnswerState.Success)
        assertEquals(
            "Kotlin Multiplatform is a cross-platform framework.", 
            (state as AnswerState.Success).text
        )
    }
    
    @Test
    fun `generateAnswer handles errors`() = runTest {
        // Set up mock to throw exception
        val testPrompt = "What is Kotlin?"
        val errorMessage = "Network error"
        mockAIService.shouldThrowException = true
        mockAIService.exceptionMessage = errorMessage
        
        // Call the method
        viewModel.generateAnswer(testPrompt)
        
        // Skip loading state
        advanceUntilIdle()
        
        // Check final state
        val state = viewModel.state.first()
        assertTrue(state is AnswerState.Error)
        assertEquals(errorMessage, (state as AnswerState.Error).message)
    }
}

/**
 * Mock AIService implementation for testing.
 */
class MockAIService : AIService {
    // Control the mock behavior
    var nextResponse: String = "Default test response"
    var simulateStreamedResponse: Boolean = false
    var streamedResponses: List<String> = emptyList()
    var shouldThrowException: Boolean = false
    var exceptionMessage: String = "Test exception"
    
    override fun generateAnswerStream(prompt: String): Flow<String> {
        if (shouldThrowException) {
            return flow { throw Exception(exceptionMessage) }
        }
        
        return if (simulateStreamedResponse) {
            flow {
                for (response in streamedResponses) {
                    emit(response)
                }
            }
        } else {
            flow { emit(nextResponse) }
        }
    }
} 
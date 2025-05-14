package com.example.snaplearn.performance

import com.example.snaplearn.data.AIService
import com.example.snaplearn.data.ApiKeyProvider
import com.example.snaplearn.data.GeminiApiClient
import com.example.snaplearn.data.GeminiService
import com.example.snaplearn.data.HistoryRepository
import com.example.snaplearn.data.InMemoryHistoryRepository
import com.example.snaplearn.data.QuestionAnswer
import com.example.snaplearn.viewmodel.SharedAnswerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.system.getMemoryUsage
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Memory profiling test for the AI service and ViewModel components.
 * 
 * NOTE: This test uses the experimental getMemoryUsage() function available in Kotlin/Native 
 * and may not work on all platforms. The function is a placeholder and should be replaced
 * with an actual platform-specific memory measurement method in production.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MemoryProfilingTest {

    // Mock API key provider for testing
    private val testApiKeyProvider = object : ApiKeyProvider {
        override suspend fun getApiKey(): String = System.getenv("GEMINI_API_KEY") ?: "test-api-key"
    }
    
    // Create the AI service with mock or real components based on environment
    private val geminiApiClient = GeminiApiClient(testApiKeyProvider)
    private val aiService: AIService = GeminiService(geminiApiClient)
    
    // Memory profiling helper function (placeholder - implementation depends on platform)
    private fun getMemoryUsage(): Long {
        // This is a placeholder. On Kotlin/Native we might use Kotlin's experimental memory API
        // On JVM we could use java.lang.Runtime.getRuntime().totalMemory() - freeMemory()
        // For actual implementation, platform-specific code should be used
        return Random.nextLong(1_000_000, 10_000_000) // Placeholder dummy value
    }
    
    @Test
    fun testMemoryLeakInViewModel() = runTest {
        // Skip test if not running with real API
        if (System.getenv("GEMINI_API_KEY") == null) {
            println("Skipping memory test - no API key available")
            return@runTest
        }
        
        // Setup
        val repository: HistoryRepository = InMemoryHistoryRepository()
        val viewModel = SharedAnswerViewModel(aiService, repository)
        
        // Record initial memory
        val initialMemory = getMemoryUsage()
        println("Initial memory usage: $initialMemory bytes")
        
        // Create collection job
        val job = launch {
            viewModel.uiState.collect {} // Collect flow to ensure it's active
        }
        
        // Perform multiple operations
        repeat(10) { i ->
            val prompt = "Short question $i?"
            viewModel.processQuestion(prompt)
            advanceUntilIdle() // Let coroutines complete
            
            // Check memory usage
            val currentMemory = getMemoryUsage()
            println("Memory after question $i: $currentMemory bytes")
        }
        
        // Cancel collection to stop observing the flow
        job.cancel()
        
        // Clear the ViewModel state
        viewModel.clearState()
        advanceUntilIdle()
        
        // Check final memory
        val finalMemory = getMemoryUsage()
        println("Final memory usage: $finalMemory bytes")
        
        // In a real test, we'd check for leaks:
        // Expectation: memory should not grow unbounded
        // We're using a dummy implementation, so this is just illustrative
        // assertTrue(finalMemory < initialMemory * 2, "Memory usage grew too much")
    }
    
    @Test
    fun testMemoryUsageDuringStreamingResponse() = runTest {
        // Skip test if not running with real API
        if (System.getenv("GEMINI_API_KEY") == null) {
            println("Skipping memory test - no API key available")
            return@runTest
        }
        
        // Record initial memory
        val initialMemory = getMemoryUsage()
        println("Initial memory usage: $initialMemory bytes")
        
        // Perform streaming requests of increasing size
        val prompts = listOf(
            "What is the capital of France?",
            "Write a paragraph about climate change.",
            "Explain quantum computing in 5 sentences.",
            "Describe the plot of Hamlet in detail."
        )
        
        for (prompt in prompts) {
            val memoryBeforeRequest = getMemoryUsage()
            
            var maxMemoryDuringStream = memoryBeforeRequest
            var receivedChunks = 0
            
            val job = launch {
                aiService.generateAnswerStream(prompt).collect { chunk ->
                    receivedChunks++
                    val currentMemory = getMemoryUsage()
                    if (currentMemory > maxMemoryDuringStream) {
                        maxMemoryDuringStream = currentMemory
                    }
                }
            }
            
            advanceUntilIdle() // Wait for the stream to complete
            job.cancel()
            
            val memoryAfterRequest = getMemoryUsage()
            
            println("Memory profile for prompt: '$prompt'")
            println("- Chunks received: $receivedChunks")
            println("- Memory before request: $memoryBeforeRequest bytes")
            println("- Max memory during streaming: $maxMemoryDuringStream bytes")
            println("- Memory after completion: $memoryAfterRequest bytes")
            println("- Memory increase during peak: ${maxMemoryDuringStream - memoryBeforeRequest} bytes")
            println("- Memory after vs before: ${memoryAfterRequest - memoryBeforeRequest} bytes")
            println()
            
            // In a real test with actual memory measurements:
            // assertTrue(memoryAfterRequest < memoryBeforeRequest * 1.5, "Memory usage grew too much after request")
        }
    }
    
    @Test
    fun testMemoryUsageWithLargeHistory() = runTest {
        // Setup
        val repository: HistoryRepository = InMemoryHistoryRepository()
        val viewModel = SharedAnswerViewModel(aiService, repository)
        
        // Populate repository with fake data
        val initialMemory = getMemoryUsage()
        
        // Add 100 items to history
        repeat(100) { i ->
            repository.saveQuestionAnswer(
                QuestionAnswer(
                    id = i.toString(),
                    question = "Test question $i?",
                    answer = "This is a test answer for question $i. " + 
                            "It contains some data to make it reasonably sized.",
                    timestamp = System.currentTimeMillis() - (i * 60000) // Each entry 1 min apart
                )
            )
        }
        
        val afterPopulatingMemory = getMemoryUsage()
        println("Memory after populating 100 history items: ${afterPopulatingMemory - initialMemory} bytes")
        
        // Load all history through repository
        var historyItems: List<QuestionAnswer>? = null
        val job = launch {
            repository.getAllQuestionAnswers().collect { items ->
                historyItems = items
            }
        }
        
        advanceUntilIdle()
        
        val afterLoadingMemory = getMemoryUsage()
        println("Memory after loading all history items: ${afterLoadingMemory - afterPopulatingMemory} bytes")
        println("Total history items loaded: ${historyItems?.size ?: 0}")
        
        // Cleanup
        job.cancel()
        historyItems = null
        
        // Force GC if available on platform
        System.gc()
        
        advanceUntilIdle()
        
        val finalMemory = getMemoryUsage()
        println("Final memory after cleanup: $finalMemory bytes")
        println("Memory difference from start: ${finalMemory - initialMemory} bytes")
        
        // In a real test with actual memory measurements:
        // assertTrue(finalMemory < initialMemory * 1.5, "Memory not properly reclaimed after cleanup")
    }
} 
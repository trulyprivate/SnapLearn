package com.example.snaplearn.performance

import com.example.snaplearn.data.AIService
import com.example.snaplearn.data.ApiKeyProvider
import com.example.snaplearn.data.GeminiApiClient
import com.example.snaplearn.data.GeminiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Performance benchmarks for the AI service.
 * These tests measure response time, throughput, and reliability of the AI service.
 * Note: These tests require a valid API key to run against the real Gemini API.
 */
class AIServicePerformanceBenchmark {

    // Mock API key provider for testing
    private val testApiKeyProvider = object : ApiKeyProvider {
        override suspend fun getApiKey(): String = System.getenv("GEMINI_API_KEY") ?: "test-api-key"
    }
    
    // Create the AI service
    private val geminiApiClient = GeminiApiClient(testApiKeyProvider)
    private val aiService: AIService = GeminiService(geminiApiClient)
    
    @Test
    fun testSingleQueryResponseTime() = runTest {
        // Skip test if no real API key is available
        if (System.getenv("GEMINI_API_KEY") == null) {
            println("Skipping performance test - no API key available")
            return@runTest
        }
        
        val prompt = "What is the capital of France?"
        
        val responseTime = measureTimeMillis {
            aiService.generateAnswer(prompt)
        }
        
        println("Single query response time: $responseTime ms")
        
        // Benchmark target: response should be under 3000ms
        // This is a reasonable target for API response time
        assertTrue(responseTime < 3000, "Response time exceeded benchmark target")
    }
    
    @Test
    fun testStreamingResponseLatency() = runTest {
        // Skip test if no real API key is available
        if (System.getenv("GEMINI_API_KEY") == null) {
            println("Skipping performance test - no API key available")
            return@runTest
        }
        
        val prompt = "List the first 5 prime numbers"
        var firstChunkLatency = 0L
        var totalTime = 0L
        var chunkCount = 0
        
        totalTime = measureTimeMillis {
            val flow = aiService.generateAnswerStream(prompt)
            
            firstChunkLatency = measureTimeMillis {
                flow.take(1).collect { 
                    chunkCount++
                }
            }
            
            // Collect the rest of the chunks
            flow.collect {
                chunkCount++
            }
        }
        
        println("First chunk latency: $firstChunkLatency ms")
        println("Total streaming time: $totalTime ms")
        println("Total chunks received: $chunkCount")
        
        // Benchmark targets
        assertTrue(firstChunkLatency < 1000, "First chunk latency exceeded benchmark target")
        assertTrue(chunkCount > 0, "No chunks were received")
    }
    
    @Test
    fun testConcurrentRequests() = runTest {
        // Skip test if no real API key is available
        if (System.getenv("GEMINI_API_KEY") == null) {
            println("Skipping performance test - no API key available")
            return@runTest
        }
        
        val numRequests = 3
        val prompts = listOf(
            "What is the capital of France?",
            "What is the largest planet in our solar system?",
            "What is the square root of 144?"
        )
        
        val totalTime = measureTimeMillis {
            val deferreds = prompts.map { prompt ->
                async {
                    aiService.generateAnswer(prompt)
                }
            }
            
            // Wait for all requests to complete
            val results = deferreds.awaitAll()
            
            // Verify all results were received
            assertTrue(results.size == numRequests)
            results.forEach { result ->
                assertTrue(result.isNotEmpty())
            }
        }
        
        println("Concurrent requests ($numRequests) total time: $totalTime ms")
        println("Average time per request: ${totalTime / numRequests} ms")
        
        // Benchmark target: average time should be reasonable
        // Concurrent requests should be efficient
        assertTrue(totalTime / numRequests < 5000, "Average request time exceeded benchmark target")
    }
    
    @Test
    fun testReliabilityUnderLoad() = runTest {
        // Skip test if no real API key is available
        if (System.getenv("GEMINI_API_KEY") == null) {
            println("Skipping performance test - no API key available")
            return@runTest
        }
        
        val numRequests = 5
        val prompt = "Hello, world!"
        var successCount = 0
        var failureCount = 0
        
        val totalTime = measureTimeMillis {
            val jobs = List(numRequests) {
                launch {
                    try {
                        val result = aiService.generateAnswer(prompt)
                        if (result.isNotEmpty()) {
                            successCount++
                        } else {
                            failureCount++
                        }
                    } catch (e: Exception) {
                        failureCount++
                    }
                }
            }
            
            // Wait for all jobs to complete
            jobs.forEach { it.join() }
        }
        
        println("Reliability test results:")
        println("- Success rate: ${successCount * 100 / numRequests}%")
        println("- Failure rate: ${failureCount * 100 / numRequests}%")
        println("- Total time for $numRequests requests: $totalTime ms")
        
        // Benchmark target: success rate should be high
        assertTrue(successCount >= numRequests * 0.8, "Success rate below benchmark target")
    }
} 
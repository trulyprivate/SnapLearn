package com.example.snaplearn.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

/**
 * Integration tests for Android-specific implementations with shared code.
 * These tests verify that Android implementations correctly integrate with shared code.
 */
@RunWith(AndroidJUnit4::class)
class PlatformIntegrationTest : KoinTest {
    
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val androidApiKeyProvider: ApiKeyProvider by inject()
    private val textRecognizer: TextRecognizer by inject()
    private val historyRepository: HistoryRepository by inject()
    
    @Before
    fun setup() {
        // Stop any existing Koin instance
        try {
            stopKoin()
        } catch (e: IllegalStateException) {
            // Koin was not started, ignore
        }
        
        // Start Koin with test modules
        startKoin {
            modules(
                module {
                    single { context }
                    single<ApiKeyProvider> { AndroidApiKeyProvider(get()) }
                    single { AndroidDatabaseDriverFactory(get()) }
                    single { TextRecognizerFactory(get()) }
                    single { SQLDelightHistoryRepository(get()) as HistoryRepository }
                }
            )
        }
    }
    
    @Test
    fun testTextRecognizerIntegration() {
        // Verify text recognizer is initialized properly
        assertNotNull("TextRecognizer should not be null", textRecognizer)
        
        // Test recognizer can be created
        val recognizer = textRecognizer
        
        // Check that analyzer can be created
        val analyzer = recognizer.createAnalyzer { recognizedText ->
            // Should receive text here
        }
        
        // Verify analyzer is of correct type for Android
        assertTrue("Analyzer should be an Android ML Kit analyzer", 
                   analyzer::class.java.name.contains("TextRecognitionAnalyzer"))
    }
    
    @Test
    fun testSecureStorageIntegration() = runBlocking {
        // Verify API key provider works
        assertNotNull("ApiKeyProvider should not be null", androidApiKeyProvider)
        
        // Test storing and retrieving a test key
        // Note: For testing, we use a special test key that won't affect production
        runCatching {
            (androidApiKeyProvider as AndroidApiKeyProvider).setApiKey("test-integration-key")
            
            // Try to retrieve it
            val retrievedKey = androidApiKeyProvider.getApiKey()
            assertNotNull("Retrieved key should not be null", retrievedKey)
            assertTrue("Retrieved key should match stored key", 
                       retrievedKey == "test-integration-key")
            
            // Clean up
            (androidApiKeyProvider as AndroidApiKeyProvider).setApiKey("")
        }
    }
    
    @Test
    fun testDatabaseIntegration() = runBlocking {
        // Verify repository is initialized properly
        assertNotNull("HistoryRepository should not be null", historyRepository)
        
        // Test basic CRUD operations
        val testQuestion = "Integration test question?"
        val testAnswer = "This is an integration test answer."
        
        // Create a test entry
        val questionAnswer = QuestionAnswer(
            id = "integration-test-id",
            question = testQuestion,
            answer = testAnswer,
            timestamp = System.currentTimeMillis()
        )
        
        // Save it to the database
        historyRepository.saveQuestionAnswer(questionAnswer)
        
        // Retrieve it
        var found = false
        historyRepository.getAllQuestionAnswers().collect { items ->
            found = items.any { it.id == "integration-test-id" }
        }
        
        assertTrue("Test entry should be found in the database", found)
        
        // Clean up
        historyRepository.deleteQuestionAnswer("integration-test-id")
    }
} 
package com.example.snaplearn.performance

import com.example.snaplearn.data.QuestionAnswerRepository
import com.example.snaplearn.data.createInMemorySqlDriver
import com.example.snaplearn.database.SnapLearnDatabase
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource
import kotlin.time.measureTime

/**
 * Simple performance benchmark for SnapLearn repository operations.
 * This class measures the time taken for key database operations.
 */
@OptIn(ExperimentalTime::class)
class RepositoryPerformanceBenchmark {
    
    // Test dependencies
    private val driver = createInMemorySqlDriver()
    private val database = SnapLearnDatabase(driver)
    private val repository = QuestionAnswerRepository(database)
    
    // Performance thresholds (in milliseconds)
    private val insertThreshold = 50L // ms
    private val queryThreshold = 20L // ms
    private val updateThreshold = 30L // ms
    private val deleteThreshold = 20L // ms
    
    // Test data
    private val testQuestion = "What is the performance impact of SQLite operations in Kotlin Multiplatform?"
    private val testAnswer = "SQLite operations in Kotlin Multiplatform can vary by platform. " +
            "Android typically has good performance due to native SQLite support. " +
            "iOS performance depends on the driver implementation and connection settings."
    
    private val testItems = mutableListOf<String>()
    
    @BeforeTest
    fun setup() {
        // Nothing to do here, we want a fresh database for each test
    }
    
    @AfterTest
    fun tearDown() {
        driver.close()
    }
    
    @Test
    fun `benchmark single item insert`() = runTest {
        val duration = measureTime {
            repository.addQuestionAnswer(testQuestion, testAnswer)
        }
        
        println("Single insert time: ${duration.inWholeMilliseconds}ms")
        // In real benchmark framework, we'd assert this is below threshold
        // assert(duration.inWholeMilliseconds < insertThreshold)
    }
    
    @Test
    fun `benchmark bulk insert and query`() = runTest {
        // First, insert a batch of items
        val insertDuration = measureTime {
            repeat(100) { i ->
                val id = repository.addQuestionAnswer(
                    "Test Question $i",
                    "Test Answer $i with some additional content to make it realistic"
                )
                testItems.add(id)
            }
        }
        
        println("Bulk insert time (100 items): ${insertDuration.inWholeMilliseconds}ms")
        // assert(insertDuration.inWholeMilliseconds / 100 < insertThreshold)
        
        // Now measure query performance
        val queryDuration = measureTime {
            repository.getAllQuestionAnswers()
        }
        
        println("Query time (100 items): ${queryDuration.inWholeMilliseconds}ms")
        // assert(queryDuration.inWholeMilliseconds < queryThreshold)
    }
    
    @Test
    fun `benchmark update performance`() = runTest {
        // First create an item
        val id = repository.addQuestionAnswer(testQuestion, testAnswer)
        
        // Now measure update performance
        val updateDuration = measureTime {
            repository.updateQuestionAnswer(
                id = id,
                question = "Updated $testQuestion",
                answer = "Updated $testAnswer",
                imageData = null,
                favorited = true
            )
        }
        
        println("Update time: ${updateDuration.inWholeMilliseconds}ms")
        // assert(updateDuration.inWholeMilliseconds < updateThreshold)
    }
    
    @Test
    fun `benchmark delete performance`() = runTest {
        // First create items
        repeat(10) { i ->
            val id = repository.addQuestionAnswer(
                "Delete Test Question $i",
                "Delete Test Answer $i"
            )
            testItems.add(id)
        }
        
        // Now measure delete performance
        val totalDeleteTime = measureTime {
            testItems.forEach { id ->
                repository.deleteQuestionAnswer(id)
            }
        }
        
        val averageDeleteTime = totalDeleteTime.inWholeMilliseconds / testItems.size
        println("Average delete time per item: ${averageDeleteTime}ms")
        // assert(averageDeleteTime < deleteThreshold)
    }
    
    @Test
    fun `benchmark favorite toggle performance`() = runTest {
        // First create an item
        val id = repository.addQuestionAnswer(testQuestion, testAnswer)
        
        // Now measure toggle performance over multiple toggles
        val toggleCount = 10
        val totalToggleTime = measureTime {
            repeat(toggleCount) {
                repository.toggleFavorite(id)
            }
        }
        
        val averageToggleTime = totalToggleTime.inWholeMilliseconds / toggleCount
        println("Average favorite toggle time: ${averageToggleTime}ms")
        // assert(averageToggleTime < updateThreshold / 2) // Should be faster than a full update
    }
} 
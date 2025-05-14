package com.example.myapplication

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.snaplearn.data.AIService
import com.example.snaplearn.data.ApiKeyProvider
import com.example.snaplearn.data.GeminiApiClient
import com.example.snaplearn.data.GeminiService
import com.example.snaplearn.data.HistoryRepository
import com.example.snaplearn.data.SQLDelightHistoryRepository
import com.example.snaplearn.viewmodel.SharedAnswerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.math.abs
import kotlin.system.measureTimeMillis

/**
 * Battery consumption test for the Android application.
 * Measures battery usage for key operations to ensure efficient power usage.
 */
@RunWith(AndroidJUnit4::class)
class BatteryUsageTest : KoinTest {
    
    private val instrumentation: Instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val aiService: AIService by inject()
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
                    single<ApiKeyProvider> { TestApiKeyProvider() }
                    single { GeminiApiClient(get()) }
                    single { GeminiService(get()) as AIService }
                    single { SQLDelightHistoryRepository(get()) as HistoryRepository }
                }
            )
        }
    }
    
    @Test
    fun testBatteryUsageDuringAIOperation() = runBlocking {
        // Skip test if running on an emulator or battery info unavailable
        if (!isBatteryInfoAvailable()) {
            println("Skipping battery test - battery information unavailable")
            return@runBlocking
        }
        
        // Get initial battery level
        val initialBatteryLevel = getBatteryLevel()
        val initialBatteryTemp = getBatteryTemperature()
        
        println("Initial battery level: $initialBatteryLevel%")
        println("Initial battery temperature: $initialBatteryTemp°C")
        
        // Measure time and perform operation
        val executionTime = measureTimeMillis {
            // Perform AI operation
            for (i in 1..5) {
                val prompt = "Test prompt $i for battery usage monitoring"
                withContext(Dispatchers.IO) {
                    try {
                        val response = aiService.generateAnswer(prompt)
                        println("Response received for prompt $i: ${response.take(50)}...")
                    } catch (e: Exception) {
                        println("Error during AI operation: ${e.message}")
                    }
                }
            }
        }
        
        // Get final battery level
        val finalBatteryLevel = getBatteryLevel()
        val finalBatteryTemp = getBatteryTemperature()
        
        println("Final battery level: $finalBatteryLevel%")
        println("Final battery temperature: $finalBatteryTemp°C")
        println("Battery level change: ${initialBatteryLevel - finalBatteryLevel}%")
        println("Battery temperature change: ${finalBatteryTemp - initialBatteryTemp}°C")
        println("Execution time: ${executionTime}ms")
        
        // Calculate power efficiency (execution time / battery percentage used)
        val batteryUsed = abs(initialBatteryLevel - finalBatteryLevel).coerceAtLeast(0.01f)
        val powerEfficiency = executionTime / batteryUsed
        
        println("Power efficiency: $powerEfficiency ms per battery %")
        
        // Verify battery usage is reasonable
        // Since battery percentage has limited resolution, we mainly check temperature
        assertTrue("Battery temperature increase should be minimal", 
                 finalBatteryTemp - initialBatteryTemp < 5.0f)
    }
    
    @Test
    fun testBatteryUsageForDatabaseOperations() = runBlocking {
        // Skip test if running on an emulator or battery info unavailable
        if (!isBatteryInfoAvailable()) {
            println("Skipping battery test - battery information unavailable")
            return@runBlocking
        }
        
        // Get initial battery level
        val initialBatteryLevel = getBatteryLevel()
        val initialBatteryTemp = getBatteryTemperature()
        
        println("Initial battery level: $initialBatteryLevel%")
        println("Initial battery temperature: $initialBatteryTemp°C")
        
        // Create test data
        val entries = List(100) { index ->
            com.example.snaplearn.data.QuestionAnswer(
                id = "test-${index}",
                question = "Battery test question $index?",
                answer = "This is a test answer for battery optimization testing with index $index",
                timestamp = System.currentTimeMillis()
            )
        }
        
        // Measure time and perform database operations
        val executionTime = measureTimeMillis {
            // Insert entries
            for (entry in entries) {
                historyRepository.saveQuestionAnswer(entry)
            }
            
            // Read entries
            var count = 0
            historyRepository.getAllQuestionAnswers().take(1).collect { items ->
                count = items.size
                println("Retrieved $count items from repository")
            }
            
            // Delete entries
            for (entry in entries) {
                historyRepository.deleteQuestionAnswer(entry.id)
            }
        }
        
        // Get final battery level
        val finalBatteryLevel = getBatteryLevel()
        val finalBatteryTemp = getBatteryTemperature()
        
        println("Final battery level: $finalBatteryLevel%")
        println("Final battery temperature: $finalBatteryTemp°C")
        println("Battery level change: ${initialBatteryLevel - finalBatteryLevel}%")
        println("Battery temperature change: ${finalBatteryTemp - initialBatteryTemp}°C")
        println("Execution time: ${executionTime}ms")
        
        // Calculate efficiency (operations per battery percentage)
        val batteryUsed = abs(initialBatteryLevel - finalBatteryLevel).coerceAtLeast(0.01f)
        val operationsPerBatteryPercent = (entries.size * 3) / batteryUsed  // 3 operations per entry
        
        println("Database operations per battery percent: $operationsPerBatteryPercent")
        
        // Verify battery usage is reasonable
        assertTrue("Battery temperature increase should be minimal", 
                 finalBatteryTemp - initialBatteryTemp < 2.0f)
    }
    
    // Helper methods to get battery information
    private fun getBatteryLevel(): Float {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        
        return if (level != -1 && scale != -1) {
            level * 100f / scale
        } else {
            -1f
        }
    }
    
    private fun getBatteryTemperature(): Float {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val temp = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
        
        return if (temp != -1) {
            temp / 10f  // Convert to degrees Celsius
        } else {
            -1f
        }
    }
    
    private fun isBatteryInfoAvailable(): Boolean {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return batteryIntent != null && 
               batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) != -1 &&
               batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1) != -1
    }
    
    // Test API key provider that returns a fixed test key
    private class TestApiKeyProvider : ApiKeyProvider {
        override suspend fun getApiKey(): String {
            return System.getenv("GEMINI_API_KEY") ?: "test-api-key"
        }
    }
} 
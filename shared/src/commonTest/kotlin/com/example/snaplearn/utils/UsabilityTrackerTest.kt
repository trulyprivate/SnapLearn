package com.example.snaplearn.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class UsabilityTrackerTest {
    
    @Test
    fun testSessionTracking() = runTest {
        val tracker = UsabilityTracker.getInstance()
        tracker.resetMetrics()
        
        // Start a session
        tracker.startSession()
        
        // Simulate some time passing
        advanceTimeBy(5000)
        
        // End the session
        val duration = tracker.endSession()
        
        // Verify the duration is approximately what we expect
        assertTrue(duration >= 5000.milliseconds, "Session duration should be at least 5000ms, was ${duration.inWholeMilliseconds}ms")
    }
    
    @Test
    fun testScreenTimeTracking() = runTest {
        val tracker = UsabilityTracker.getInstance()
        tracker.resetMetrics()
        
        // Track screen opening
        tracker.trackScreenOpen("TestScreen")
        
        // Simulate some time passing
        advanceTimeBy(3000)
        
        // Track screen closing
        val screenTime = tracker.trackScreenClose("TestScreen")
        
        // Verify the screen time is approximately what we expect
        assertNotNull(screenTime, "Screen time should not be null")
        assertTrue(screenTime >= 3000.milliseconds, "Screen time should be at least 3000ms, was ${screenTime.inWholeMilliseconds}ms")
    }
    
    @Test
    fun testOperationTimingTracking() = runTest {
        val tracker = UsabilityTracker.getInstance()
        tracker.resetMetrics()
        
        // Track several operations
        tracker.trackOperationTiming("TestOperation", 100.milliseconds)
        tracker.trackOperationTiming("TestOperation", 200.milliseconds)
        tracker.trackOperationTiming("TestOperation", 300.milliseconds)
        
        // Get the average time
        val avgTime = tracker.getAverageOperationTime("TestOperation")
        
        // Verify the average is correct
        assertNotNull(avgTime, "Average time should not be null")
        assertEquals(200.milliseconds, avgTime, "Average time should be 200ms")
        
        // Verify the operation timings flow
        val operationTimings = tracker.operationTimings.first()
        assertNotNull(operationTimings["TestOperation"], "Operation timings should contain TestOperation")
        assertEquals(3, operationTimings["TestOperation"]?.size, "TestOperation should have 3 timing samples")
    }
    
    @Test
    fun testErrorTracking() = runTest {
        val tracker = UsabilityTracker.getInstance()
        tracker.resetMetrics()
        
        // Track some interactions to establish a baseline
        tracker.trackInteraction("ButtonClick")
        tracker.trackInteraction("ButtonClick")
        tracker.trackInteraction("ButtonClick")
        tracker.trackInteraction("ButtonClick")
        
        // Track an error
        tracker.trackError("NetworkError")
        
        // Verify the error rate
        val errorRate = tracker.getErrorRate("NetworkError")
        assertEquals(25f, errorRate, "Error rate should be 25%")
        
        // Verify the errors flow
        val errors = tracker.errors.first()
        assertEquals(1, errors["NetworkError"], "NetworkError count should be 1")
    }
    
    @Test
    fun testInteractionTracking() = runTest {
        val tracker = UsabilityTracker.getInstance()
        tracker.resetMetrics()
        
        // Track various interactions
        tracker.trackInteraction("ButtonClick")
        tracker.trackInteraction("ButtonClick")
        tracker.trackInteraction("Swipe")
        
        // Verify the interactions flow
        val interactions = tracker.interactions.first()
        assertEquals(2, interactions["ButtonClick"], "ButtonClick count should be 2")
        assertEquals(1, interactions["Swipe"], "Swipe count should be 1")
    }
    
    @Test
    fun testMetricsSummary() = runTest {
        val tracker = UsabilityTracker.getInstance()
        tracker.resetMetrics()
        
        // Set up some sample data
        tracker.startSession()
        tracker.trackScreenOpen("TestScreen")
        tracker.trackOperationTiming("TestOperation", 150.milliseconds)
        tracker.trackInteraction("ButtonClick")
        tracker.trackError("NetworkError")
        
        // Get the metrics summary
        val summary = tracker.getMetricsSummary()
        
        // Verify the summary contains expected sections
        assertTrue(summary.contains("=== USABILITY METRICS SUMMARY ==="), "Summary should have a title")
        assertTrue(summary.contains("Session Duration:"), "Summary should include session duration")
        assertTrue(summary.contains("OPERATION TIMINGS:"), "Summary should include operation timings")
        assertTrue(summary.contains("ERROR RATES:"), "Summary should include error rates")
        assertTrue(summary.contains("USER INTERACTIONS:"), "Summary should include user interactions")
    }
} 
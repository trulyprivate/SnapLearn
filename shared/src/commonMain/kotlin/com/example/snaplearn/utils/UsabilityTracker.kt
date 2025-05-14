package com.example.snaplearn.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * A shared utility for tracking usability metrics across both platforms.
 * This helps in collecting data for usability testing and user experience optimization.
 */
class UsabilityTracker {
    
    // Singleton instance
    companion object {
        private val instance = UsabilityTracker()
        
        fun getInstance(): UsabilityTracker = instance
    }
    
    // Track session information
    private var sessionStartTime: Long = 0
    private var screenOpenTimes = mutableMapOf<String, Long>()
    
    // Track operation metrics
    private val _operationTimings = MutableStateFlow<Map<String, List<Duration>>>(emptyMap())
    val operationTimings: StateFlow<Map<String, List<Duration>>> = _operationTimings.asStateFlow()
    
    // Track error rates
    private val _errors = MutableStateFlow<Map<String, Int>>(emptyMap())
    val errors: StateFlow<Map<String, Int>> = _errors.asStateFlow()
    
    // Track user interactions
    private val _interactions = MutableStateFlow<Map<String, Int>>(emptyMap())
    val interactions: StateFlow<Map<String, Int>> = _interactions.asStateFlow()
    
    /**
     * Start tracking the current user session.
     */
    fun startSession() {
        sessionStartTime = Clock.System.now().toEpochMilliseconds()
    }
    
    /**
     * End the current user session and return the duration.
     */
    fun endSession(): Duration {
        val sessionEndTime = Clock.System.now().toEpochMilliseconds()
        val sessionDuration = sessionEndTime - sessionStartTime
        return sessionDuration.toDuration(DurationUnit.MILLISECONDS)
    }
    
    /**
     * Track when a screen is opened for time-on-screen metrics.
     */
    fun trackScreenOpen(screenName: String) {
        screenOpenTimes[screenName] = Clock.System.now().toEpochMilliseconds()
    }
    
    /**
     * Track when a screen is closed and calculate time spent on screen.
     */
    fun trackScreenClose(screenName: String): Duration? {
        val openTime = screenOpenTimes[screenName] ?: return null
        val closeTime = Clock.System.now().toEpochMilliseconds()
        val timeOnScreen = closeTime - openTime
        return timeOnScreen.toDuration(DurationUnit.MILLISECONDS)
    }
    
    /**
     * Track the time taken for an operation to complete.
     */
    fun trackOperationTiming(operationName: String, duration: Duration) {
        val currentTimings = _operationTimings.value.toMutableMap()
        val timingsForOperation = currentTimings.getOrDefault(operationName, emptyList()).toMutableList()
        timingsForOperation.add(duration)
        currentTimings[operationName] = timingsForOperation
        _operationTimings.value = currentTimings
    }
    
    /**
     * Track an error that occurred in the application.
     */
    fun trackError(errorCategory: String) {
        val currentErrors = _errors.value.toMutableMap()
        val count = currentErrors.getOrDefault(errorCategory, 0)
        currentErrors[errorCategory] = count + 1
        _errors.value = currentErrors
    }
    
    /**
     * Track user interactions with the application.
     */
    fun trackInteraction(interactionType: String) {
        val currentInteractions = _interactions.value.toMutableMap()
        val count = currentInteractions.getOrDefault(interactionType, 0)
        currentInteractions[interactionType] = count + 1
        _interactions.value = currentInteractions
    }
    
    /**
     * Get average operation time for a specific operation.
     */
    fun getAverageOperationTime(operationName: String): Duration? {
        val timings = _operationTimings.value[operationName] ?: return null
        if (timings.isEmpty()) return null
        
        val totalMillis = timings.sumOf { it.inWholeMilliseconds }
        return (totalMillis / timings.size).toDuration(DurationUnit.MILLISECONDS)
    }
    
    /**
     * Get error rate as a percentage of total operations.
     */
    fun getErrorRate(errorCategory: String): Float {
        val errorCount = _errors.value[errorCategory] ?: 0
        val totalInteractions = _interactions.value.values.sum()
        
        return if (totalInteractions > 0) {
            (errorCount.toFloat() / totalInteractions) * 100
        } else {
            0f
        }
    }
    
    /**
     * Reset all tracked metrics.
     */
    fun resetMetrics() {
        sessionStartTime = 0
        screenOpenTimes.clear()
        _operationTimings.value = emptyMap()
        _errors.value = emptyMap()
        _interactions.value = emptyMap()
    }
    
    /**
     * Get a summary of all usability metrics as a formatted string.
     */
    fun getMetricsSummary(): String {
        val summary = StringBuilder()
        summary.append("=== USABILITY METRICS SUMMARY ===\n\n")
        
        // Session info
        val sessionDuration = if (sessionStartTime > 0) {
            val currentTime = Clock.System.now().toEpochMilliseconds()
            (currentTime - sessionStartTime).toDuration(DurationUnit.MILLISECONDS)
        } else {
            Duration.ZERO
        }
        summary.append("Session Duration: ${formatDuration(sessionDuration)}\n\n")
        
        // Operation timings
        summary.append("OPERATION TIMINGS:\n")
        _operationTimings.value.forEach { (operation, timings) ->
            val avgTime = getAverageOperationTime(operation)
            summary.append("- $operation: ${formatDuration(avgTime)} avg (${timings.size} samples)\n")
        }
        summary.append("\n")
        
        // Errors
        summary.append("ERROR RATES:\n")
        _errors.value.forEach { (category, count) ->
            val rate = getErrorRate(category)
            summary.append("- $category: $count errors (${String.format("%.2f", rate)}%)\n")
        }
        summary.append("\n")
        
        // Interactions
        summary.append("USER INTERACTIONS:\n")
        _interactions.value.forEach { (type, count) ->
            summary.append("- $type: $count\n")
        }
        
        return summary.toString()
    }
    
    private fun formatDuration(duration: Duration?): String {
        if (duration == null) return "N/A"
        return when {
            duration.inWholeSeconds < 1 -> "${duration.inWholeMilliseconds}ms"
            duration.inWholeMinutes < 1 -> "${duration.inWholeSeconds}s"
            else -> "${duration.inWholeMinutes}m ${duration.inWholeSeconds % 60}s"
        }
    }
} 
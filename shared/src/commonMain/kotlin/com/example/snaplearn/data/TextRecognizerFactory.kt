package com.example.snaplearn.data

/**
 * Factory interface for creating platform-specific TextRecognizer instances.
 * This will be implemented with expect/actual pattern.
 */
expect class TextRecognizerFactory() {
    /**
     * Creates a new instance of a platform-specific TextRecognizer.
     */
    fun createTextRecognizer(): TextRecognizer
} 
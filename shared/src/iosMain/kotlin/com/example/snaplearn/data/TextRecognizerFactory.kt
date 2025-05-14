package com.example.snaplearn.data

/**
 * iOS implementation of TextRecognizerFactory.
 * Creates a Vision framework based text recognizer.
 */
actual class TextRecognizerFactory {
    /**
     * Creates a new IOSTextRecognizer that uses Vision framework.
     */
    actual fun createTextRecognizer(): TextRecognizer {
        return IOSTextRecognizer()
    }
} 
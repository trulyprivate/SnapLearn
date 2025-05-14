package com.example.snaplearn.data

import android.content.Context

/**
 * Android implementation of TextRecognizerFactory.
 * Uses the application context to create an ML Kit based text recognizer.
 */
actual class TextRecognizerFactory(private val context: Context) {
    /**
     * Creates a new AndroidTextRecognizer that uses ML Kit.
     */
    actual fun createTextRecognizer(): TextRecognizer {
        return AndroidTextRecognizer(context)
    }
} 
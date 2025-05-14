package com.example.snaplearn.data

/**
 * Common interface for text recognition across platforms.
 * This will be implemented differently on Android (using ML Kit) and iOS (using Vision framework).
 */
interface TextRecognizer {
    /**
     * Analyzes the image and returns recognized text.
     * 
     * @param imageBytes The image data as a ByteArray
     * @return RecognizedText containing the results
     */
    suspend fun recognizeText(imageBytes: ByteArray): RecognizedText
    
    /**
     * Stops text recognition and releases resources.
     */
    fun close()
}

/**
 * Data class representing recognized text from an image.
 */
data class RecognizedText(
    val text: String,
    val blocks: List<TextBlock> = emptyList(),
    val confidence: Float = 0.0f
)

/**
 * Represents a block of text from the recognition process.
 */
data class TextBlock(
    val text: String,
    val boundingBox: BoundingBox? = null,
    val confidence: Float = 0.0f
)

/**
 * Simple representation of a bounding box.
 */
data class BoundingBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) 
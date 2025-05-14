package com.example.snaplearn.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Android implementation of TextRecognizer using ML Kit.
 */
class AndroidTextRecognizer(private val context: Context) : TextRecognizer {
    // Create ML Kit text recognizer
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    /**
     * Recognizes text in an image using ML Kit's text recognition.
     */
    override suspend fun recognizeText(imageBytes: ByteArray): RecognizedText = suspendCancellableCoroutine { continuation ->
        try {
            // Convert ByteArray to Bitmap
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            val image = InputImage.fromBitmap(bitmap, 0)
            
            // Process the image
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Convert ML Kit result to our common model
                    val textBlocks = visionText.textBlocks.map { block ->
                        val rect = block.boundingBox
                        TextBlock(
                            text = block.text,
                            boundingBox = rect?.let { 
                                BoundingBox(
                                    left = it.left.toFloat(),
                                    top = it.top.toFloat(),
                                    right = it.right.toFloat(),
                                    bottom = it.bottom.toFloat()
                                ) 
                            },
                            confidence = 0.0f // ML Kit doesn't provide confidence scores
                        )
                    }
                    
                    val fullText = visionText.text
                    continuation.resume(RecognizedText(fullText, textBlocks))
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
                
            continuation.invokeOnCancellation {
                // Handle cancellation if needed
            }
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }
    
    /**
     * Closes the recognizer and releases resources.
     */
    override fun close() {
        recognizer.close()
    }
} 
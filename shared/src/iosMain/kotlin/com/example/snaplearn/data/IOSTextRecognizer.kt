package com.example.snaplearn.data

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreFoundation.CFAutorelease
import platform.CoreFoundation.CFBridgingRelease
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.create
import platform.UIKit.UIImage
import platform.Vision.VNDetectTextRectanglesRequest
import platform.Vision.VNDetectTextRectanglesRequestHandler
import platform.Vision.VNImageRequestHandler
import platform.Vision.VNRecognizeTextRequest
import platform.Vision.VNRecognizeTextRequestCompletionHandler
import platform.Vision.VNRecognizeTextRequestRevision3
import platform.Vision.VNRequestTextRecognitionLevelAccurate
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * iOS implementation of TextRecognizer using Vision framework.
 */
@OptIn(ExperimentalForeignApi::class)
class IOSTextRecognizer : TextRecognizer {
    /**
     * Recognizes text in an image using Vision framework.
     */
    override suspend fun recognizeText(imageBytes: ByteArray): RecognizedText = suspendCancellableCoroutine { continuation ->
        try {
            // Convert ByteArray to NSData
            val nsData = NSData.create(bytes = imageBytes.refTo(0), length = imageBytes.size.toULong())
            
            // Create UIImage from NSData
            val uiImage = UIImage.imageWithData(nsData) ?: throw IllegalArgumentException("Failed to create UIImage from data")
            
            // Create a text recognition request
            val request = VNRecognizeTextRequest { request, error ->
                if (error != null) {
                    continuation.resumeWithException(Exception(error.localizedDescription))
                    return@VNRecognizeTextRequest
                }
                
                val observations = request.results ?: emptyList()
                val textBlocks = observations.map { observation ->
                    val boundingBox = observation.boundingBox?.let { box ->
                        BoundingBox(
                            left = box.origin.x.toFloat(),
                            top = box.origin.y.toFloat(),
                            right = (box.origin.x + box.size.width).toFloat(),
                            bottom = (box.origin.y + box.size.height).toFloat()
                        )
                    }
                    
                    val confidence = observation.confidence?.toFloat() ?: 0.0f
                    val text = observation.topCandidates(1).firstOrNull()?.string ?: ""
                    
                    TextBlock(text, boundingBox, confidence)
                }
                
                val fullText = textBlocks.joinToString(" ") { it.text }
                val averageConfidence = if (textBlocks.isNotEmpty()) {
                    textBlocks.sumOf { it.confidence.toDouble() }.toFloat() / textBlocks.size
                } else {
                    0.0f
                }
                
                continuation.resume(RecognizedText(fullText, textBlocks, averageConfidence))
            }
            
            // Configure the request
            request.setRevision(VNRecognizeTextRequestRevision3)
            request.setRecognitionLevel(VNRequestTextRecognitionLevelAccurate)
            
            // Create a request handler
            val requestHandler = VNImageRequestHandler.alloc().initWithData(
                imageData = nsData,
                options = mapOf<Any?, Any>()
            )
            
            // Perform the request
            requestHandler.performRequests(listOf(request), null)
            
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }
    
    /**
     * Closes the recognizer (no-op on iOS as Vision framework manages its own resources).
     */
    override fun close() {
        // No explicit cleanup needed for Vision framework
    }
} 
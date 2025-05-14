package com.example.snaplearn.utils

/**
 * Utility class for text processing operations that are common across platforms.
 */
object TextProcessingUtils {
    
    /**
     * Cleans up recognized text by removing extra whitespace, correcting common OCR errors, etc.
     *
     * @param text The raw recognized text
     * @return Cleaned up text
     */
    fun cleanText(text: String): String {
        var result = text.trim()
        
        // Replace multiple spaces with a single space
        result = result.replace(Regex("\\s+"), " ")
        
        // Replace common OCR errors
        result = result.replace(Regex("l\\s"), "I ")  // lowercase l often mistaken for uppercase I
        result = result.replace(Regex("0(?=[a-zA-Z])"), "O")  // 0 often mistaken for O when followed by letters
        
        return result
    }
    
    /**
     * Extracts potential questions from the text.
     * Looks for sentences ending with question marks.
     *
     * @param text The input text
     * @return List of questions found in the text
     */
    fun extractQuestions(text: String): List<String> {
        // Split by sentence
        val sentences = text.split(Regex("(?<=[.?!])\\s+"))
        
        // Filter for questions (sentences ending with '?')
        return sentences.filter { it.trim().endsWith("?") }
    }
    
    /**
     * Attempts to categorize the type of text (e.g., paragraph, list, question, etc.)
     *
     * @param text The input text
     * @return The detected text type
     */
    fun categorizeTextType(text: String): TextType {
        val cleanText = text.trim()
        
        // Check if it's a list (starts with bullets, numbers, etc.)
        if (cleanText.lines().any { line -> line.trim().matches(Regex("^(\\d+\\.|•|\\*|-)\\s+.*")) }) {
            return TextType.LIST
        }
        
        // Check if it's a question
        if (cleanText.endsWith("?")) {
            return TextType.QUESTION
        }
        
        // Check if it's a math equation
        if (cleanText.matches(Regex(".*[+\\-*/=^√∫].*")) && cleanText.matches(Regex(".*\\d+.*"))) {
            return TextType.MATH_EQUATION
        }
        
        // Default to paragraph
        return TextType.PARAGRAPH
    }
    
    /**
     * Extracts important keywords from the text.
     *
     * @param text The input text
     * @return List of keywords
     */
    fun extractKeywords(text: String): List<String> {
        // This is a simplified implementation
        // In a real application, you would use NLP or a keyword extraction algorithm
        
        // For now, we'll just extract capitalized words and remove common words
        val commonWords = setOf("the", "and", "or", "but", "in", "on", "at", "to", "a", "an", "of", "for", "with")
        val words = text.split(Regex("\\s+"))
        
        return words
            .filter { it.length > 3 } // Only words longer than 3 characters
            .filter { word -> !commonWords.contains(word.lowercase()) } // Remove common words
            .map { it.replace(Regex("[.,;:!?'\"]"), "") } // Remove punctuation
            .filter { it.isNotEmpty() }
            .distinct()
    }
}

/**
 * Represents different types of text content.
 */
enum class TextType {
    PARAGRAPH,
    LIST,
    QUESTION,
    MATH_EQUATION
} 
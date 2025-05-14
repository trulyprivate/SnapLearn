package com.example.snaplearn.di

import com.example.snaplearn.data.ApiKeyProvider
import com.example.snaplearn.data.GeminiApiClient
import com.example.snaplearn.data.GeminiService
import com.example.snaplearn.data.IOSApiKeyProvider
import com.example.snaplearn.data.IOSDatabaseDriverFactory
import com.example.snaplearn.data.SQLDelightHistoryRepository
import com.example.snaplearn.data.TextRecognizerFactory
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * iOS-specific implementation of the platform module.
 */
actual fun AppModule.Companion.platformModule(): Module = module {
    // iOS-specific implementations
    single<ApiKeyProvider> { IOSApiKeyProvider() }
    single { IOSDatabaseDriverFactory() }
    single { TextRecognizerFactory() }
    
    // Services
    single { GeminiApiClient(get()) }
    single { GeminiService(get()) as com.example.snaplearn.data.AIService }
    
    // Repositories
    single { SQLDelightHistoryRepository(get()) as com.example.snaplearn.data.HistoryRepository }
} 
package com.example.snaplearn.di

import android.content.Context
import com.example.snaplearn.data.AndroidApiKeyProvider
import com.example.snaplearn.data.AndroidDatabaseDriverFactory
import com.example.snaplearn.data.ApiKeyProvider
import com.example.snaplearn.data.GeminiApiClient
import com.example.snaplearn.data.GeminiService
import com.example.snaplearn.data.SQLDelightHistoryRepository
import com.example.snaplearn.data.TextRecognizerFactory
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android-specific implementation of the platform module.
 */
actual fun AppModule.Companion.platformModule(): Module = module {
    single { get<Context>().applicationContext }
    
    // Android-specific implementations
    single<ApiKeyProvider> { AndroidApiKeyProvider(get()) }
    single { AndroidDatabaseDriverFactory(get()) }
    single { TextRecognizerFactory(get()) }
    
    // Services
    single { GeminiApiClient(get()) }
    single { GeminiService(get()) as com.example.snaplearn.data.AIService }
    
    // Repositories
    single { SQLDelightHistoryRepository(get()) as com.example.snaplearn.data.HistoryRepository }
} 
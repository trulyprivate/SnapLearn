package com.example.snaplearn.di

import com.example.snaplearn.data.AIService
import com.example.snaplearn.data.HistoryRepository
import com.example.snaplearn.data.TextRecognizerFactory
import com.example.snaplearn.viewmodel.SharedAnswerViewModel
import com.example.snaplearn.viewmodel.SharedHistoryViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Singleton class for managing dependency injection across the application.
 */
class AppModule {
    /**
     * Initialize Koin dependency injection.
     */
    fun initKoin(appModule: Module): KoinApplication {
        return startKoin {
            modules(
                commonModule,
                platformModule(),
                appModule
            )
        }
    }
    
    companion object {
        /**
         * Common module with shared dependencies.
         */
        val commonModule = module {
            // ViewModels
            factory { SharedAnswerViewModel(get()) }
            factory { SharedHistoryViewModel(get()) }
        }
        
        /**
         * Provides platform-specific module. This is expected to be implemented
         * differently for Android and iOS.
         */
        expect fun platformModule(): Module
    }
} 
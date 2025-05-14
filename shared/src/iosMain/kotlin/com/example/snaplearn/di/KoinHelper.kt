package com.example.snaplearn.di

import com.example.snaplearn.data.AIService
import com.example.snaplearn.data.HistoryRepository
import com.example.snaplearn.data.TextRecognizer
import com.example.snaplearn.data.TextRecognizerFactory
import com.example.snaplearn.viewmodel.SharedAnswerViewModel
import com.example.snaplearn.viewmodel.SharedHistoryViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Helper class to initialize Koin in iOS and provide access to dependencies.
 */
object KoinHelper {
    /**
     * Initialize Koin for iOS.
     */
    fun initKoin() {
        startKoin {
            modules(
                AppModule.commonModule,
                AppModule.platformModule(),
                module { }
            )
        }
    }
}

/**
 * Provides access to dependencies in iOS.
 */
class IosKoinHelper : KoinComponent {
    fun getTextRecognizer(): TextRecognizer {
        return get<TextRecognizerFactory>().createTextRecognizer()
    }
    
    fun getAIService(): AIService {
        return get()
    }
    
    fun getHistoryRepository(): HistoryRepository {
        return get()
    }
    
    fun getSharedAnswerViewModel(): SharedAnswerViewModel {
        return get()
    }
    
    fun getSharedHistoryViewModel(): SharedHistoryViewModel {
        return get()
    }
} 
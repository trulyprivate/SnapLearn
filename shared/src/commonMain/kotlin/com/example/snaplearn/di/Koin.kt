package com.example.snaplearn.di

import com.example.snaplearn.data.DatabaseDriverFactory
import com.example.snaplearn.data.QuestionAnswerRepository
import com.example.snaplearn.data.QuestionAnswerRepositoryImpl
import com.example.snaplearn.shared.database.SnapLearnDatabase
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        platformModule(),
        commonModule
    )
}

// Called by iOS
fun initKoin() = initKoin {}

expect fun platformModule(): Module

val commonModule = module {
    single { 
        val driverFactory: DatabaseDriverFactory = get()
        SnapLearnDatabase(driverFactory.createDriver()) 
    }
    single<QuestionAnswerRepository> { QuestionAnswerRepositoryImpl(get()) }
} 
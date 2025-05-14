package com.example.snaplearn.di

import android.content.Context
import com.example.snaplearn.data.DatabaseDriverFactory
import com.example.snaplearn.utils.initStorage
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DatabaseDriverFactory() }
}

fun initKoinAndroid(context: Context) {
    val databaseDriverFactory: DatabaseDriverFactory = org.koin.core.context.GlobalContext.get().get()
    databaseDriverFactory.init(context)
    
    // Initialize storage
    initStorage(context)
} 
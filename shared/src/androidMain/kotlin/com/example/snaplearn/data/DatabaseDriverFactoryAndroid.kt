package com.example.snaplearn.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.snaplearn.shared.database.SnapLearnDatabase

actual class DatabaseDriverFactory {
    private lateinit var context: Context
    
    fun init(context: Context) {
        this.context = context.applicationContext
    }
    
    actual fun createDriver(): SqlDriver {
        if (!::context.isInitialized) {
            throw IllegalStateException("DatabaseDriverFactory not initialized. Call init(context) first.")
        }
        return AndroidSqliteDriver(SnapLearnDatabase.Schema, context, "snaplearn.db")
    }
} 
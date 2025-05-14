package com.example.snaplearn.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.snaplearn.database.SnapLearnDatabase

/**
 * Android implementation of DatabaseDriverFactory.
 */
class AndroidDatabaseDriverFactory(private val context: Context) : DatabaseDriverFactory {
    /**
     * Creates a SqlDriver for the SnapLearn database on Android.
     */
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = SnapLearnDatabase.Schema,
            context = context,
            name = "snaplearn.db"
        )
    }
} 
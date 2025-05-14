package com.example.snaplearn.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.snaplearn.database.SnapLearnDatabase

/**
 * Android implementation of creating an in-memory SQL driver for testing.
 */
actual fun createInMemorySqlDriver(): SqlDriver {
    val context = ApplicationProvider.getApplicationContext<Context>()
    return AndroidSqliteDriver(
        schema = SnapLearnDatabase.Schema,
        context = context,
        name = null // null name creates an in-memory database
    )
} 
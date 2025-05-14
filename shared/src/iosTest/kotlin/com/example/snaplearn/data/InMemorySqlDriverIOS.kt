package com.example.snaplearn.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.snaplearn.database.SnapLearnDatabase

/**
 * iOS implementation of creating an in-memory SQL driver for testing.
 */
actual fun createInMemorySqlDriver(): SqlDriver {
    return NativeSqliteDriver(
        schema = SnapLearnDatabase.Schema,
        name = ":memory:", // Special name for in-memory database in SQLite
        maxReaderConnections = 1 // Recommended for tests
    )
} 
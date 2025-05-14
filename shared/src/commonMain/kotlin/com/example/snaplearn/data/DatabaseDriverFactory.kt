package com.example.snaplearn.data

import app.cash.sqldelight.db.SqlDriver

/**
 * Interface for creating platform-specific SQLDelight database drivers.
 */
interface DatabaseDriverFactory {
    /**
     * Creates a SqlDriver for the SnapLearn database.
     */
    fun createDriver(): SqlDriver
} 
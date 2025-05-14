package com.example.snaplearn.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.snaplearn.database.SnapLearnDatabase

/**
 * iOS implementation of DatabaseDriverFactory.
 */
class IOSDatabaseDriverFactory : DatabaseDriverFactory {
    /**
     * Creates a SqlDriver for the SnapLearn database on iOS.
     */
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = SnapLearnDatabase.Schema,
            name = "snaplearn.db"
        )
    }
} 
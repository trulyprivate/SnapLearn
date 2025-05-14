package com.example.snaplearn.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.snaplearn.shared.database.SnapLearnDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(SnapLearnDatabase.Schema, "snaplearn.db")
    }
} 
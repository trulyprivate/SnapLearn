package com.example.myapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Item::class],
    version = 1
)
abstract class ItemsDatabase:RoomDatabase() {
    abstract val dao:ItemDao
}
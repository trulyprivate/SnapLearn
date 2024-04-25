package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Upsert
    suspend fun usertItem(item:Item)

    @Delete
    suspend fun deleteItem(item:Item)

    @Query("SELECT * FROM item ORDER BY dateTime")
    fun getItems():Flow<List<Item>>
}
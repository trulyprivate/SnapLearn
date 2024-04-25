package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(
    val question:String,
    val answer:String,
    val dateTime:Long,
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0
)

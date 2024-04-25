package com.example.myapplication.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.myapplication.data.Item

data class ItemState (val items:List<Item> = emptyList(),
                      val question:MutableState<String> = mutableStateOf(""),
                      val answer:MutableState<String> = mutableStateOf("")
)
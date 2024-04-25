package com.example.myapplication.presentation

import com.example.myapplication.data.Item

sealed interface ItemsEvent {
    data class DeleteItem(val item:Item) :ItemsEvent
}
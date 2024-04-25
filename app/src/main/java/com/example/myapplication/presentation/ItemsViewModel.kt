package com.example.myapplication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.QuestionAnswer
import com.example.myapplication.data.Item
import com.example.myapplication.data.ItemDao
import kotlinx.coroutines.launch

class ItemsViewModel(
    private val dao:ItemDao
):ViewModel() {
    fun onEvent(event:ItemsEvent){
        when (event){
            is ItemsEvent.DeleteItem -> {
                viewModelScope.launch { dao.deleteItem(event.item) }
            }
        }
    }

    fun getQuestionsAndAnswers(): List<QuestionAnswer> {
//        viewModelScope.launch { dao.getItems() }

        val questionsAndAnswers = listOf(
            QuestionAnswer("What is Compose?", "A modern UI framework for Android"),
            QuestionAnswer("What are previews?", "A way to see your composables in action"),
                    QuestionAnswer("What is Compose?", "A modern UI framework for Android"),
        QuestionAnswer("What are previews?", "A way to see your composables in action"),
            QuestionAnswer("What is Compose?", "A modern UI framework for Android"),
            QuestionAnswer("What are previews?", "A way to see your composables in action "),
            QuestionAnswer("What is Compose?", "A modern UI framework for Android"),
            QuestionAnswer("What are previews?", "A way to see your composables in action"),
            QuestionAnswer("What is Compose?", "A modern UI framework for Android"),
            QuestionAnswer("What are previews?", "A way to see your composables in action"),
            QuestionAnswer("What is Compose?", "A modern UI framework for Android"),
            QuestionAnswer("What are previews?", "A way to see your composables in action"),
            QuestionAnswer("What is Compose?", "A modern UI framework for Android"),
            QuestionAnswer("What are previews?", "A way to see your composables in action "),
            QuestionAnswer("What is Compose?", "A modern UI framework for Android"),
            QuestionAnswer("What are previews?", "A way to see your composables in action")
        )

        return questionsAndAnswers
    }
}
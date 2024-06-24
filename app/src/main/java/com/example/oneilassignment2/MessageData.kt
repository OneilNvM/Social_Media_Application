package com.example.oneilassignment2

data class MessageData(
    val message: String,
    val date: String,
    val chatId: Int,
    val studentId: Int,
    val isUser: Boolean = false
)

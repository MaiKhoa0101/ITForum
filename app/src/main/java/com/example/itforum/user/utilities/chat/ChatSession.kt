package com.example.itforum.utilities.chat

data class ChatSession(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val messages: List<Message>
)
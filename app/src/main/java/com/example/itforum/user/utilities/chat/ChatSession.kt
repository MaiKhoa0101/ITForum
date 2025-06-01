package com.example.itforum.utilities.chat
import java.util.UUID

data class ChatSession(
//    val id: Long = System.currentTimeMillis(),
//    val title: String,
//    val messages: List<Message>
    val id: String = UUID.randomUUID().toString(), // 🔧 Thêm id mặc định
    val title: String,
    val messages: List<Message>
)
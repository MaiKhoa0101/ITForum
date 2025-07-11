package com.example.itforum.user.utilities.chat



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val userId: String,
    val text: String,
    val isUser: Boolean,
    val sender: String,
    val timestamp: Long
)



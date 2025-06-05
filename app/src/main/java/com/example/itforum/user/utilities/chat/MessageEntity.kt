package com.example.itforum.user.utilities.chat



import androidx.room.Entity
import androidx.room.PrimaryKey
//@Entity(tableName = "messages")
//data class MessageEntity(
//    @PrimaryKey(autoGenerate = true) val messageId: Int = 0,
//    val sessionId: String,
//    val sender: String,
//    val content: String,
//    val timestamp: Long,
//    val userId: String // ✅ Thêm userId
//)

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



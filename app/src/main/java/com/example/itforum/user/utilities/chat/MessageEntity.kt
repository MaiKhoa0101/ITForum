package com.example.itforum.user.utilities.chat



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")

data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val messageId: Int = 0,
    val sessionId: String,
    val text: String,
    val isUser: Boolean
) {
    companion object {
        fun loading(sessionId: String): MessageEntity {
            return MessageEntity(
                sessionId = sessionId,
                text = "Đang trả lời...",
                isUser = false
            )
        }
    }
}



//data class MessageEntity(
//    @PrimaryKey(autoGenerate = true) val messageId: Int = 0,
//    val sessionId: String,
//    val text: String,
//    val isUser: Boolean
//)

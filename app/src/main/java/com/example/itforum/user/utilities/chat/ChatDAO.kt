package com.example.itforum.user.utilities.chat

import androidx.room.*

@Dao
interface ChatDao {
    // Session
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSessionEntity)

    @Query("SELECT * FROM chat_sessions ORDER BY timestamp DESC")
    suspend fun getAllSessions(): List<ChatSessionEntity>

    @Delete
    suspend fun deleteSession(session: ChatSessionEntity)

    // Message
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE sessionId = :sessionId ORDER BY messageId ASC")
    suspend fun getMessagesForSession(sessionId: String): List<MessageEntity>

    @Query("DELETE FROM messages WHERE sessionId = :sessionId")
    suspend fun deleteMessagesForSession(sessionId: String)
    @Delete
    suspend fun delete(session: ChatSessionEntity)
}

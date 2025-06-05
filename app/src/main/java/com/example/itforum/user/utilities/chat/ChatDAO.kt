package com.example.itforum.user.utilities.chat

import androidx.room.*

@Dao
interface ChatDao {
    // Session
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSessionEntity)

    @Query("SELECT * FROM chat_sessions WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getAllSessions(userId: String): List<ChatSessionEntity>

    @Delete
    suspend fun deleteSession(session: ChatSessionEntity)

    // Message
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE sessionId = :sessionId AND userId = :userId ORDER BY timestamp ASC")
    suspend fun getMessagesForSession(sessionId: String, userId: String): List<MessageEntity>

    @Query("DELETE FROM messages WHERE sessionId = :sessionId AND userId = :userId")
    suspend fun deleteMessagesForSession(sessionId: String, userId: String)

    @Delete
    suspend fun delete(session: ChatSessionEntity)
}



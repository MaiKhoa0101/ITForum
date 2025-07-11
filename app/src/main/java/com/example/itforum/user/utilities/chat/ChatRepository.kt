package com.example.itforum.user.utilities.chat

class ChatRepository(private val chatDao: ChatDao) {

    suspend fun getAllSessions(userId: String): List<ChatSessionEntity> =
        chatDao.getAllSessions(userId)

    suspend fun getMessagesForSession(sessionId: String, userId: String): List<MessageEntity> =
        chatDao.getMessagesForSession(sessionId, userId)

    suspend fun deleteEmptySessions(userId: String) {
        val allSessions = chatDao.getAllSessions(userId)
        for (session in allSessions) {
            val messages = chatDao.getMessagesForSession(session.sessionId, userId)
            if (session.title == "Chat mới" && messages.isEmpty()) {
                chatDao.delete(session)
            }
        }
    }

    suspend fun insertSession(session: ChatSessionEntity) {
        chatDao.insertSession(session)
    }

    suspend fun insertMessage(message: MessageEntity) {
        chatDao.insertMessage(message)
    }

    suspend fun deleteSession(session: ChatSessionEntity) {
        chatDao.delete(session)
    }
}


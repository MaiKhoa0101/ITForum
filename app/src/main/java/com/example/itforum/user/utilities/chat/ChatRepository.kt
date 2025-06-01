package com.example.itforum.user.utilities.chat




class ChatRepository(private val chatDao: ChatDao) {





    suspend fun insertSession(session: ChatSessionEntity) {
        chatDao.insertSession(session)
    }

    suspend fun getAllSessions(): List<ChatSessionEntity> {
        return chatDao.getAllSessions()
    }



    suspend fun insertMessage(message: MessageEntity) {
        chatDao.insertMessage(message)
    }

    suspend fun getMessagesForSession(sessionId: String): List<MessageEntity> {
        return chatDao.getMessagesForSession(sessionId)
    }


    suspend fun deleteEmptySessions() {
        val allSessions = chatDao.getAllSessions()
        allSessions.forEach { session ->
            val messages = chatDao.getMessagesForSession(session.id)
            if (session.title == "Chat mới" && messages.isEmpty()) {
                chatDao.delete(session)
            }
        }
    }
    suspend fun deleteSession(session: ChatSessionEntity) {
        chatDao.delete(session)
    }

}

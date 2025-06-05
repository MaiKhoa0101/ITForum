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

//package com.example.itforum.user.utilities.chat
//
//
//
//
//class ChatRepository(private val chatDao: ChatDao) {
//
//    suspend fun getAllSessions(userId: String): List<ChatSessionEntity> = dao.getAllSessions(userId)
//    suspend fun getMessagesForSession(sessionId: String, userId: String): List<MessageEntity> = dao.getMessagesForSession(sessionId, userId)
//    suspend fun deleteEmptySessions(userId: String) = dao.deleteEmptySessions(userId)
//
//
//
//
//    suspend fun insertSession(session: ChatSessionEntity) {
//        chatDao.insertSession(session)
//    }
//
//    suspend fun getAllSessions(): List<ChatSessionEntity> {
//        return chatDao.getAllSessions()
//    }
//
//
//
//    suspend fun insertMessage(message: MessageEntity) {
//        chatDao.insertMessage(message)
//    }
//
//    suspend fun getMessagesForSession(sessionId: String): List<MessageEntity> {
//        return chatDao.getMessagesForSession(sessionId)
//    }
//
//
//    suspend fun deleteEmptySessions() {
//        val allSessions = chatDao.getAllSessions()
//        allSessions.forEach { session ->
//            val messages = chatDao.getMessagesForSession(session.id)
//            if (session.title == "Chat mới" && messages.isEmpty()) {
//                chatDao.delete(session)
//            }
//        }
//    }
//    suspend fun deleteSession(session: ChatSessionEntity) {
//        chatDao.delete(session)
//    }
//
//}

package com.example.itforum.user.utilities.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel(
    private val repository: ChatRepository,
    private val userId: String
) : ViewModel() {

    private val _sessions = MutableStateFlow<List<ChatSessionEntity>>(emptyList())
    val sessions = _sessions.asStateFlow()

    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId = _currentSessionId.asStateFlow()
    init {
        viewModelScope.launch {
            repository.deleteEmptySessions(userId)
            _sessions.value = repository.getAllSessions(userId)
            _currentSessionId.value = _sessions.value.firstOrNull()?.sessionId
        }
    }

    private fun loadSessions() {
        viewModelScope.launch {
            _sessions.value = repository.getAllSessions(userId)
        }
    }

    fun loadMessages(sessionId: String) {
        _currentSessionId.value = sessionId
        viewModelScope.launch {
            _messages.value = repository.getMessagesForSession(sessionId, userId)
        }
    }

    fun setCurrentSession(sessionId: String) {
        _currentSessionId.value = sessionId
        loadMessages(sessionId)
    }

    fun createNewSession() {
        val sessionId = UUID.randomUUID().toString()
        val session = ChatSessionEntity(
            sessionId = sessionId,
            title = "Chat mới",
            userId = userId,
            timestamp = System.currentTimeMillis()
        )
        _currentSessionId.value = session.sessionId
        viewModelScope.launch {
            repository.insertSession(session)
            _sessions.value = repository.getAllSessions(userId)
            _messages.value = emptyList()
        }
    }

    fun deleteSession(session: ChatSessionEntity) {
        viewModelScope.launch {
            repository.deleteSession(session)
            _sessions.value = repository.getAllSessions(userId)
            if (_currentSessionId.value == session.sessionId) {
                _messages.value = emptyList()
            }
        }
    }

    fun sendMessage(input: String, isUser: Boolean) {
        if (input.isBlank()) {
            Log.w("OpenRouter", "⚠️ Không gửi hoặc lưu tin nhắn trống.")
            return
        }

        var sessionId = _currentSessionId.value
        if (sessionId == null && isUser) {
            val newSessionId = UUID.randomUUID().toString()
            val newSession = ChatSessionEntity(
                sessionId = newSessionId,
                title = "Chat mới",
                userId = userId,
                timestamp = System.currentTimeMillis()
            )
            sessionId = newSession.sessionId
            _currentSessionId.value = sessionId
            viewModelScope.launch {
                repository.insertSession(newSession)
                _sessions.value = repository.getAllSessions(userId)
            }
        }

        if (sessionId == null) return

        val message = MessageEntity(
            id = UUID.randomUUID().toString(),
            sessionId = sessionId,
            userId = userId,
            text = input,
            isUser = isUser,
            sender = if (isUser) userId else "bot",
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            // Gửi và lưu tin nhắn người dùng
            repository.insertMessage(message)
            _messages.value = repository.getMessagesForSession(sessionId, userId)

            try {
                // Gửi prompt đến backend
                val response = RetrofitInstance.chatAiService.getChatResponse(AiRequest(input))

                val botMessage = MessageEntity(
                    id = UUID.randomUUID().toString(),
                    sessionId = sessionId,
                    userId = userId,
                    text = response.response,
                    isUser = false,
                    sender = "bot",
                    timestamp = System.currentTimeMillis()
                )
                repository.insertMessage(botMessage)
                _messages.value = repository.getMessagesForSession(sessionId, userId)
            } catch (e: Exception) {
                Log.e("ChatAI", "❌ Lỗi khi gọi AI: ${e.message}")
                val errorMessage = MessageEntity(
                    id = UUID.randomUUID().toString(),
                    sessionId = sessionId,
                    userId = userId,
                    text = "❌ AI gặp lỗi khi xử lý: ${e.message}",
                    isUser = false,
                    sender = "bot",
                    timestamp = System.currentTimeMillis()
                )
                repository.insertMessage(errorMessage)
                _messages.value = repository.getMessagesForSession(sessionId, userId)
            }
        }
    }


    fun updateSessionTitleIfNeeded(title: String) {
        val sessionId = _currentSessionId.value ?: return
        val existing = _sessions.value.find { it.sessionId == sessionId }
        if (existing != null && existing.title == "Chat mới") {
            val updated = existing.copy(title = title)
            viewModelScope.launch {
                repository.insertSession(updated)
                _sessions.value = repository.getAllSessions(userId)
            }
        }
    }

    fun getCurrentSessionId(): String? = _currentSessionId.value


}

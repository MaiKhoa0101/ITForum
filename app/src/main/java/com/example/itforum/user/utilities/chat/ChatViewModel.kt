package com.example.itforum.user.utilities.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.user.utilities.chat.ChatSessionEntity
import com.example.itforum.user.utilities.chat.MessageEntity
import com.example.itforum.user.utilities.chat.ChatRepository
import com.example.itforum.utilities.chat.OpenRouterApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    private val _sessions = MutableStateFlow<List<ChatSessionEntity>>(emptyList())
    val sessions = _sessions.asStateFlow()

    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId = _currentSessionId.asStateFlow()

    init {
        viewModelScope.launch {
            repository.deleteEmptySessions()
            _sessions.value = repository.getAllSessions()
            _currentSessionId.value = _sessions.value.firstOrNull()?.id
        }
    }

    private fun loadSessions() {
        viewModelScope.launch {
            _sessions.value = repository.getAllSessions()
        }
    }

    fun loadMessages(sessionId: String) {
        _currentSessionId.value = sessionId
        viewModelScope.launch {
            _messages.value = repository.getMessagesForSession(sessionId)
        }
    }

    fun setCurrentSession(sessionId: String) {
        _currentSessionId.value = sessionId
        loadMessages(sessionId)
    }

    fun createNewSession() {
        val session = ChatSessionEntity(
            id = UUID.randomUUID().toString(),
            title = "Chat mới"
        )
        _currentSessionId.value = session.id
        viewModelScope.launch {
            repository.insertSession(session)
            _sessions.value = repository.getAllSessions()
            _messages.value = emptyList()
        }
    }

    fun deleteSession(session: ChatSessionEntity) {
        viewModelScope.launch {
            repository.deleteSession(session)
            _sessions.value = repository.getAllSessions()
            if (_currentSessionId.value == session.id) {
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
            val newSession = ChatSessionEntity(
                id = UUID.randomUUID().toString(),
                title = "Chat mới"
            )
            sessionId = newSession.id
            _currentSessionId.value = sessionId
            viewModelScope.launch {
                repository.insertSession(newSession)
                _sessions.value = repository.getAllSessions()
            }
        }

        if (sessionId == null) return

        val message = MessageEntity(sessionId = sessionId, text = input, isUser = isUser)

        viewModelScope.launch {
            repository.insertMessage(message)
            _messages.value = repository.getMessagesForSession(sessionId)
        }

        if (isUser) {
            Log.d("OpenRouter", "🟢 Gửi câu hỏi: $input")

            OpenRouterApiClient.generateText(input) { response ->
                Log.d("OpenRouter", "🟢 Callback được gọi với phản hồi: $response")

                viewModelScope.launch {
                    val botMessage = MessageEntity(
                        sessionId = sessionId,
                        text = response ?: "❌ Lỗi khi kết nối AI",
                        isUser = false
                    )
                    repository.insertMessage(botMessage)
                    _messages.value = repository.getMessagesForSession(sessionId)
                }
            }
        }
    }

    fun updateSessionTitleIfNeeded(title: String) {
        val sessionId = _currentSessionId.value ?: return
        val existing = _sessions.value.find { it.id == sessionId }
        if (existing != null && existing.title == "Chat mới") {
            val updated = existing.copy(title = title)
            viewModelScope.launch {
                repository.insertSession(updated)
                _sessions.value = repository.getAllSessions()
            }
        }
    }

    fun getCurrentSessionId(): String? = _currentSessionId.value
}



//package com.example.itforum.user.utilities.chat
//
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.itforum.user.utilities.chat.ChatSessionEntity
//import com.example.itforum.user.utilities.chat.MessageEntity
//import com.example.itforum.user.utilities.chat.ChatRepository
//import com.example.itforum.utilities.chat.OpenRouterApiClient
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import java.util.*
//
//class ChatViewModel(private val repository: ChatRepository) : ViewModel() {
//
//    private val _sessions = MutableStateFlow<List<ChatSessionEntity>>(emptyList())
//    val sessions = _sessions.asStateFlow()
//    private val API_KEY =
//        "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"
//    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
//    val messages = _messages.asStateFlow()
//
//    private val _currentSessionId = MutableStateFlow<String?>(null)
//    val currentSessionId = _currentSessionId.asStateFlow()
//
//    init {
//        viewModelScope.launch {
//            repository.deleteEmptySessions() // ✅ Xoá những session không có message
//            _sessions.value = repository.getAllSessions()
//            _currentSessionId.value = _sessions.value.firstOrNull()?.id
//        }
//    }
//
//
////    init {
////        viewModelScope.launch {
////            val allSessions = repository.getAllSessions()
////            if (allSessions.isEmpty()) {
////                val session = ChatSessionEntity(
////                    id = UUID.randomUUID().toString(),
////                    title = "Chat mới"
////                )
////                repository.insertSession(session)
////                setCurrentSession(session.id) // ✅ Gán session mới
////            } else {
////                setCurrentSession(allSessions.first().id) // ✅ Gán session đầu tiên
////            }
////            _sessions.value = repository.getAllSessions()
////        }
////    }
//
//
//    private fun loadSessions() {
//        viewModelScope.launch {
//            _sessions.value = repository.getAllSessions()
//        }
//    }
//
//    fun loadMessages(sessionId: String) {
//        _currentSessionId.value = sessionId
//
//        viewModelScope.launch {
//            _messages.value = repository.getMessagesForSession(sessionId)
//        }
//    }
//
//    fun setCurrentSession(sessionId: String) {
//        _currentSessionId.value = sessionId
//
//        loadMessages(sessionId)
//    }
//
//    fun createNewSession() {
//        val session = ChatSessionEntity(
//            id = UUID.randomUUID().toString(),
//            title = "Chat mới"
//        )
//        _currentSessionId.value = session.id // ✅ Sửa lại
//        viewModelScope.launch {
//            repository.insertSession(session)
//            _sessions.value = repository.getAllSessions()
//            _messages.value = emptyList()
//        }
//    }
//
//
//
////    fun deleteSession(session: ChatSessionEntity) {
////        viewModelScope.launch {
////            repository.deleteSession(session)
////            _sessions.value = repository.getAllSessions()
////            if (_currentSessionId.value == session.id) {
////                _messages.value = emptyList()
////            }
////        }
////    }
//
//    fun sendMessage(input: String, isUser: Boolean) {
//        if (input.isBlank()) {
//            Log.w("OpenRouter", "⚠️ Không gửi hoặc lưu tin nhắn trống.")
//            return
//        }
//
//        // ✅ Chỉ tạo session nếu là người dùng và chưa có session
//        var sessionId = _currentSessionId.value
//        if (sessionId == null && isUser) {
//            val newSession = ChatSessionEntity(
//                id = UUID.randomUUID().toString(),
//                title = "Chat mới"
//            )
//            sessionId = newSession.id
//            _currentSessionId.value = sessionId
//            viewModelScope.launch {
//                repository.insertSession(newSession)
//                _sessions.value = repository.getAllSessions()
//            }
//        }
//
//        // Nếu vẫn không có sessionId (bot trả lời nhưng chưa có session), thì bỏ
//        if (sessionId == null) return
//
//        val message = MessageEntity(sessionId = sessionId, text = input, isUser = isUser)
//
//        viewModelScope.launch {
//            repository.insertMessage(message)
//            _messages.value = repository.getMessagesForSession(sessionId)
//        }
//
//        if (isUser) {
//            Log.d("OpenRouter", "🟢 Gửi câu hỏi: $input")
//
//            OpenRouterApiClient.generateText(input) { response ->
//                Log.d("OpenRouter", "🟢 Callback được gọi với phản hồi: $response")
//
//                viewModelScope.launch {
//                    val botMessage = MessageEntity(
//                        sessionId = sessionId,
//                        text = response ?: "❌ Lỗi khi kết nối AI",
//                        isUser = false
//                    )
//                    repository.insertMessage(botMessage)
//                    _messages.value = repository.getMessagesForSession(sessionId)
//                }
//            }
//        }
//    }
//
//
//
//
//fun deleteSession(session: ChatSessionEntity) {
//    viewModelScope.launch {
//        repository.deleteSession(session)
//        _sessions.value = repository.getAllSessions()
//        if (_currentSessionId.value == session.id) {
//            _messages.value = emptyList()
//        }
//    }
//}
//
//
//    fun updateSessionTitleIfNeeded(title: String) {
//        val sessionId = _currentSessionId.value ?: return
//        val existing = _sessions.value.find { it.id == sessionId }
//        if (existing != null && existing.title == "Chat mới") {
//            val updated = existing.copy(title = title)
//            viewModelScope.launch {
//                repository.insertSession(updated)
//                _sessions.value = repository.getAllSessions()
//            }
//        }
//    }
//
//
//
//    fun getCurrentSessionId(): String? = _currentSessionId.value // ✅ Lấy value
//
//}

package com.example.itforum.user.utilities.chat

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.itforum.user.utilities.chat.viewmodel.ChatViewModel
import com.example.itforum.user.utilities.chat.viewmodel.ChatViewModelFactory
import com.example.itforum.utilities.chat.ChatHistoryScreen
import com.example.itforum.utilities.chat.ChatScreen
import com.example.itforum.utilities.chat.ChatSession
import com.example.itforum.utilities.chat.Message

@Composable
fun ChatAIApp(
    context: Context = LocalContext.current,
    onExitToHome: () -> Unit
) {
    val navController = rememberNavController()

    // Get userId from SharedPreferences
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", "") ?: ""

    // Setup Room + Repository + ViewModel
    val db = remember { ChatDatabase.getDatabase(context) }
    val repository = remember { ChatRepository(db.chatDao()) }
    val factory = remember { ChatViewModelFactory(repository, userId) }
    val viewModel: ChatViewModel = viewModel(factory = factory)

    val sessions by viewModel.sessions.collectAsState()
    val messages by viewModel.messages.collectAsState()

    // Tạo session mặc định nếu chưa có
    LaunchedEffect(sessions) {
        if (sessions.isEmpty()) {
            viewModel.createNewSession()
        }
    }


    NavHost(navController = navController, startDestination = "chat") {
        composable("chat") {
            val currentSessionId by viewModel.currentSessionId.collectAsState()

            if (currentSessionId != null) {
                ChatScreen(
                    navController = navController,
                    messages = messages.map { Message(it.text, it.isUser) },
                    onSend = { input ->
                        viewModel.sendMessage(input, isUser = true)
                        viewModel.updateSessionTitleIfNeeded(input)
                    },
                    onShowHistory = { navController.navigate("history") },
                    onBackToHome = onExitToHome
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Không có đoạn chat nào được chọn", color = Color.Gray)
                }
            }
        }

        composable("history") {
            ChatHistoryScreen(
                sessions = sessions.map { ChatSession(it.sessionId, it.title, userId, emptyList()) },
                onNewChat = {
                    viewModel.createNewSession()
                    navController.navigate("chat")
                },
                onSelectSession = { session ->
                    viewModel.setCurrentSession(session.id)
                    navController.navigate("chat")
                },
                onDeleteSession = {
                    viewModel.deleteSession(
                        ChatSessionEntity(
                            sessionId = it.id,
                            title = it.title,
                            userId = userId,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                },
                onBackToChat = { navController.navigate("chat") }
            )
        }
    }
}



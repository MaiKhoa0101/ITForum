package com.example.itforum.utilities.chat

import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.itforum.utilities.chat.ChatSession
import com.example.itforum.utilities.chat.OpenRouterApiClient


@Composable
fun ChatAIApp(onExitToHome: () -> Unit) {
    val navController = rememberNavController()
    val sessions = remember { mutableStateListOf<ChatSession>() }
    val currentSession = remember { mutableStateOf<ChatSession?>(null) }

    LaunchedEffect(Unit) {
        if (sessions.isEmpty()) {
            val initial = ChatSession(title = "Chat mới", messages = emptyList())
            sessions.add(initial)
            currentSession.value = initial
        }
    }

    NavHost(navController = navController, startDestination = "chat") {
        composable("chat") {
            currentSession.value?.let { session ->
                val messages = remember {
                    mutableStateListOf<Message>().apply { addAll(session.messages) }
                }

                ChatScreen(
                    navController = navController,
                    messages = messages,
                    onSend = { input ->
                        messages.add(Message(input, true))
                        OpenRouterApiClient.generateText(input) { reply ->
                            messages.add(
                                reply?.let { Message(it, false) }
                                    ?: Message("Đã xảy ra lỗi khi kết nối với AI.", false)
                            )
                        }
                    },
                    onShowHistory = { navController.navigate("history") },
                    onBackToHome = onExitToHome // ✅ gọi ra ngoài
                )
            }
        }

        composable("history") {
            ChatHistoryScreen(
                sessions = sessions,
                onNewChat = {
                    val newSession = ChatSession(title = "Chat mới", messages = listOf())
                    sessions.add(0, newSession)
                    currentSession.value = newSession
                    navController.navigate("chat")
                },
                onSelectSession = { session ->
                    currentSession.value = session
                    navController.navigate("chat")
                },
                onDeleteSession = { sessionToDelete ->
                    sessions.remove(sessionToDelete)
                    if (sessions.isEmpty()) {
                        currentSession.value = null
                    }
                },
                onBackToChat = {
                    navController.navigate("chat")
                }
            )

        }
    }
}



//@Composable
//fun ChatAIApp() {
//    val navController = rememberNavController()
//    val sessions = remember { mutableStateListOf<ChatSession>() }
//    val currentSession = remember { mutableStateOf<ChatSession?>(null) }
//
//    // Khởi tạo phiên đầu tiên nếu chưa có
//    LaunchedEffect(Unit) {
//        if (sessions.isEmpty()) {
//            val initial = ChatSession(
//                title = "Chat mới",
//                messages = emptyList()
//            )
//            sessions.add(initial)
//            currentSession.value = initial
//        }
//    }
//
//    NavHost(navController = navController, startDestination = "chat") {
//        composable("chat") {
//            currentSession.value?.let { session ->
//                val messages = remember { mutableStateListOf<Message>().apply { addAll(session.messages) } }
//
//                ChatScreen(
//                    navController = navController,
//                    messages = messages,
//                    onSend = { input ->
//                        messages.add(Message(input, true))
//
//                        // ✅ Nếu là tin nhắn đầu tiên, cập nhật tiêu đề
//                        if (session.title == "Chat mới" && session.messages.none { it.isUser }) {
//                            val updatedSession = session.copy(
//                                title = input.take(30),
//                                messages = messages.toList()
//                            )
//                            val index = sessions.indexOfFirst { it.id == session.id }
//                            if (index != -1) sessions[index] = updatedSession
//                            currentSession.value = updatedSession
//                        }
//
//                        // ✅ Gọi API
//                        OpenRouterApiClient.generateText(input) { reply ->
//                            messages.add(
//                                if (reply != null)
//                                    Message(reply, isUser = false)
//                                else
//                                    Message("Đã xảy ra lỗi khi kết nối với AI.", isUser = false)
//                            )
//
//                            // ✅ Sau khi nhận được phản hồi, cập nhật lại session
//                            val updated = currentSession.value!!.copy(messages = messages.toList())
//                            val index = sessions.indexOfFirst { it.id == updated.id }
//                            if (index != -1) sessions[index] = updated
//                            currentSession.value = updated
//                        }
//                    },
//                    onShowHistory = {
//                        navController.navigate("history")
//                    }
//                )
//            }
//        }
//
//        composable("history") {
//            ChatHistoryScreen(
//                sessions = sessions,
//                onNewChat = {
//                    val newSession = ChatSession(title = "Chat mới", messages = listOf())
//                    sessions.add(0, newSession)
//                    currentSession.value = newSession
//                    navController.navigate("chat")
//                },
//                onSelectSession = { session ->
//                    currentSession.value = session
//                    navController.navigate("chat")
//                },
//                onDeleteSession = { sessionToDelete ->
//                    sessions.remove(sessionToDelete)
//                    if (sessions.isEmpty()) {
//                        currentSession.value = null
//                    }
//                },
//                onBackToChat = {
//                    navController.navigate("chat")
//                }
//            )
//        }
//    }
//}





//        val session = currentSession!!
//        val messages = remember { mutableStateListOf<Message>().apply { addAll(session.messages) } }
//
//
//        fun sendMessageToBot(userMessage: String) {
//            messages.add(Message(userMessage, isUser = true))
//
//            OpenRouterApiClient.generateText(userMessage) { reply ->
//                if (reply != null) {
//                    messages.add(Message(reply, isUser = false))
//                } else {
//                    messages.add(Message("Đã xảy ra lỗi khi kết nối với AI.", isUser = false))
//                }
//            }
//        }

//        ChatScreen(
//            messages = messages,
//            onSend = { userInput ->
//                sendMessageToBot(userInput)
//            }
//        )
//    }}


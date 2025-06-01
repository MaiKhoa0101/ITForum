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

import com.example.itforum.utilities.chat.ChatHistoryScreen
import com.example.itforum.utilities.chat.ChatScreen
import com.example.itforum.utilities.chat.ChatSession
import com.example.itforum.utilities.chat.Message
import com.example.itforum.user.utilities.chat.ChatViewModel

@Composable
fun ChatAIApp(
    context: Context = LocalContext.current,
    onExitToHome: () -> Unit
) {
    val navController = rememberNavController()

    // Setup Room + Repository + ViewModel
    val db = remember { ChatDatabase.getDatabase(context) }
    val repository = remember { ChatRepository(db.chatDao()) }
    val factory = remember { ChatViewModelFactory(repository) }
    val viewModel: ChatViewModel = viewModel(factory = factory)



    val sessions by viewModel.sessions.collectAsState()
    val messages by viewModel.messages.collectAsState()

    // ✅ Tạo session mặc định nếu chưa có
    LaunchedEffect(sessions) {
        if (sessions.isEmpty()) {
            viewModel.createNewSession()
        }
    }

    NavHost(navController = navController, startDestination = "chat") {
        composable("chat") {
            val currentSessionId by viewModel.currentSessionId.collectAsState()


            // ✅ Chỉ hiển thị ChatScreen nếu đã có session được chọn
            if (currentSessionId != null) {
                ChatScreen(
                    navController = navController,
                    messages = messages.map { Message(it.text, it.isUser) },
                    onSend = { input ->
                        viewModel.sendMessage(input, isUser = true)
                        viewModel.updateSessionTitleIfNeeded(input)

                        // Đã xử lý bên trong viewModel rồi

                    },
                    onShowHistory = { navController.navigate("history") },
                    onBackToHome = onExitToHome
                )
            }else {
                // fallback UI để không bị trắng màn hình
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
                sessions = sessions.map { ChatSession(it.id, it.title, emptyList()) },
                onNewChat = {
                    viewModel.createNewSession()
                    navController.navigate("chat")
                },
                onSelectSession = { session ->
                    viewModel.setCurrentSession(session.id)    // ✅ Gán session
                    navController.navigate("chat")             // ✅ Chuyển sang chat
                },

                onDeleteSession = {
                    viewModel.deleteSession(ChatSessionEntity(it.id, it.title))
                },
                onBackToChat = { navController.navigate("chat") }
            )
        }


    }
}



//        composable("chat") {
//            ChatScreen(
//                navController = navController,
//                messages = messages.map { Message(it.text, it.isUser) },
//                onSend = { input ->
//                    viewModel.sendMessage(input, isUser = true)
//                    viewModel.updateSessionTitleIfNeeded(input)
//
//                    OpenRouterApiClient.generateText(input) { reply ->
//                        viewModel.sendMessage(reply ?: "Lỗi khi kết nối AI", isUser = false)
//                    }
//                },
//                onShowHistory = { navController.navigate("history") },
//                onBackToHome = onExitToHome
//            )
//        }



//@Composable
//fun ChatAIApp(onExitToHome: () -> Unit) {
//    val navController = rememberNavController()
//    val sessions = remember { mutableStateListOf<ChatSession>() }
//    val currentSession = remember { mutableStateOf<ChatSession?>(null) }
//
//    LaunchedEffect(Unit) {
//        if (sessions.isEmpty()) {
//            val initial = ChatSession(title = "Chat mới", messages = emptyList())
//            sessions.add(initial)
//            currentSession.value = initial
//        }
//    }
//
//    NavHost(navController = navController, startDestination = "chat") {
//        composable("chat") {
//            currentSession.value?.let { session ->
//                val messages = remember {
//                    mutableStateListOf<Message>().apply { addAll(session.messages) }
//                }
//
//                ChatScreen(
//                    navController = navController,
//                    messages = messages,
////                    onSend = { input ->
////                        messages.add(Message(input, true))
////                        OpenRouterApiClient.generateText(input) { reply ->
////                            messages.add(
////                                reply?.let { Message(it, false) }
////                                    ?: Message("Đã xảy ra lỗi khi kết nối với AI.", false)
////                            )
////                        }
////                    }
////                    ,
//                    onSend = { input ->
//                        val userMessage = Message(input, true)
//                        messages.add(userMessage)
//
//                        // ✅ Cập nhật title nếu là chat mới
//                        if (currentSession.value?.title == "Chat mới") {
//                            currentSession.value = currentSession.value?.copy(title = input)
//                        }
//
//                        // ✅ Cập nhật lại session hiện tại với messages mới
//                        currentSession.value = currentSession.value?.copy(
//                            messages = messages.toList()
//                        )
//
//                        // ✅ Ghi đè session mới vào danh sách sessions
//                        currentSession.value?.let { updated ->
//                            val index = sessions.indexOfFirst { it.id == updated.id }
//                            if (index != -1) {
//                                sessions[index] = updated
//                            }
//                        }
//
//                        // Gọi API và xử lý phản hồi
//                        OpenRouterApiClient.generateText(input) { reply ->
//                            val aiMessage = reply?.let { Message(it, false) }
//                                ?: Message("Đã xảy ra lỗi khi kết nối với AI.", false)
//
//                            messages.add(aiMessage)
//
//                            currentSession.value = currentSession.value?.copy(
//                                messages = messages.toList()
//                            )
//
//                            currentSession.value?.let { updated ->
//                                val index = sessions.indexOfFirst { it.id == updated.id }
//                                if (index != -1) {
//                                    sessions[index] = updated
//                                }
//                            }
//                        }
//                    },
//                            onShowHistory = { navController.navigate("history") },
//                    onBackToHome = onExitToHome // ✅ gọi ra ngoài
//                )
//            }
//        }
//
//        composable("history") {
//            ChatHistoryScreen(
//                sessions = sessions,
//                onNewChat = {
//                    val newSession = ChatSession(title = "Chat mới", messages = listOf())
//                    sessions.add(0, newSession)         // thêm vào đầu danh sách
//                    currentSession.value = newSession   // đặt làm session hiện tại
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
//
//        }
//    }
//}




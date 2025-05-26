package com.example.itforum.utilities.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.itforum.R
//import com.example.itforum.home.goToHome
@Composable
fun ChatScreen(
    navController: NavController,
    messages: List<Message>,
    onSend: (String) -> Unit,
    onShowHistory: () -> Unit,
    onBackToHome: () -> Unit
) {
    var input by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(1.dp)
    ) {
        // Header
        TopAppBar(
            backgroundColor = Color(0XFF00AEFF),
            modifier = Modifier.height(130.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 15.dp)
                )  {
                    Image(
                        painter = painterResource(id = R.drawable.chat),
                        contentDescription = "chat",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chat AI", fontSize = 30.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 4.dp))
                }
            },
            navigationIcon = {
                IconButton(onClick = { onBackToHome()
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.back_white),
                        contentDescription = "back",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(start = 4.dp, top = 15.dp, end = 8.dp, bottom = 2.dp)
                    )
                }
            },
            actions = {
                IconButton(onClick = { onShowHistory() }) {
                    Image(
                        painter = painterResource(id = R.drawable.more),
                        contentDescription = "back",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(start = 4.dp, top = 15.dp, end = 8.dp, bottom = 2.dp)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                MessageBubble(message)
            }
        }

        // Input field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Nhập nội dung") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xFFF0F0F0),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            IconButton(
                onClick = {
                    if (input.isNotBlank()) {
                        onSend(input.trim())
                        input = ""
                    }
                },
                enabled = input.isNotBlank() // chỉ cho bấm khi có nội dung
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Gửi",
                    tint = if (input.isNotBlank()) Color(0xFF00BFFF) else Color.Gray
                )
            }

        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val isUser = message.isUser
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (isUser) Color(0xFFD3D3D3) else Color(0xFFE0E0E0),
                    RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Text(text = message.text)
        }
    }
}

// Message data class
data class Message(val text: String, val isUser: Boolean)

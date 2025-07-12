package com.example.itforum.utilities.chat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.itforum.R
import com.example.itforum.utilities.chat.ChatSession
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

//@Composable
//fun ChatHistoryScreen(
//    sessions: List<ChatSession>,
//    onNewChat: () -> Unit,
//    onSelectSession: (ChatSession) -> Unit,
//    onDeleteSession: (ChatSession) -> Unit,
//    onBackToChat: () -> Unit
//) {
//    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
//        // Header
//        TopAppBar(
//            backgroundColor = Color(0xFF00AEFF),
//            modifier = Modifier.height(130.dp),
//            title = {
//                Text("Tùy chọn", color = Color.White, fontSize=30.sp,modifier = Modifier.padding(bottom = 4.dp, top = 20.dp))
//            },
//            navigationIcon = {
//                IconButton(onClick = { onBackToChat() }) { // ✅ gọi callback
//                    Image(
//                        painter = painterResource(id = R.drawable.back_white),
//                        contentDescription = "back",
//                        modifier = Modifier
//                            .size(40.dp)
//                            .padding(start = 4.dp, top = 15.dp, end = 8.dp, bottom = 2.dp)
//                    )
//                }
//            }
//
//        )
//
//        // Nút tạo chat mới
//        Button(
//            onClick = onNewChat,
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth(),
//            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB2EBF2))
//        ) {
//            Text("Tạo đoạn chat mới", color = Color.Black, fontSize = 16.sp)
//        }
//
//        // Lịch sử trò chuyện
//        Text("Lịch sử", modifier = Modifier.padding(start = 16.dp, top = 8.dp))
//
//        LazyColumn {
//            items(sessions) { session ->
//                HistoryItem(
//                    session = session,
//                    onSelect = onSelectSession,
//                    onDelete = onDeleteSession
//                )
//            }
//
//        }
//    }
//}
//
//@Composable
//fun HistoryItem(
//    session: ChatSession,
//    onSelect: (ChatSession) -> Unit,
//    onDelete: (ChatSession) -> Unit
//) {
//    var expanded by remember {
//        mutableStateOf(false) }
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp)
//            .clickable { onSelect(session) },
//        elevation = 4.dp,
//        shape = RoundedCornerShape(12.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(session.title, modifier = Modifier.weight(1f), fontSize= 18.sp)
//
//            Box {
//                IconButton(onClick = { expanded = true }) {
//                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
//                }
//
//                DropdownMenu(
//                    expanded = expanded,
//                    onDismissRequest = { expanded = false }
//                ) {
//                    DropdownMenuItem(onClick = {
//                        expanded = false
//                        onDelete(session)
//                    }) {
//                        Text("Xóa")
//                    }
//                }
//            }
//        }
//    }
//}
@Composable
fun ChatHistoryScreen(
    sessions: List<ChatSession>,
    onNewChat: () -> Unit,
    onSelectSession: (ChatSession) -> Unit,
    onDeleteSession: (ChatSession) -> Unit,
    onBackToChat: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        TopAppBar(
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.height(115.dp),
            title = {
                Text(
                    "Tùy chọn",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(bottom = 4.dp, top = 20.dp)
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackToChat) {
                    Image(
                        painter = painterResource(id = R.drawable.back_white),
                        contentDescription = "back",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(start = 4.dp, top = 15.dp, end = 8.dp, bottom = 2.dp)
                    )
                }
            }
        )

        // Button tạo chat mới
        Button(
            onClick = onNewChat,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )

        ) {
            Text(
                "Tạo đoạn chat mới",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontSize = 16.sp
            )
        }

        // Label lịch sử
        Text(
            "Lịch sử",
            modifier = Modifier.padding(start = 16.dp, top = 8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyColumn {
            items(sessions) { session ->
                HistoryItem(
                    session = session,
                    onSelect = onSelectSession,
                    onDelete = onDeleteSession
                )
            }
        }
    }
}

@Composable
fun HistoryItem(
    session: ChatSession,
    onSelect: (ChatSession) -> Unit,
    onDelete: (ChatSession) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onSelect(session) },
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                session.title,
                modifier = Modifier.weight(1f),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(onClick = {
                        expanded = false
                        onDelete(session)
                    }) {
                        Text("Xóa")
                    }
                }
            }
        }
    }
}

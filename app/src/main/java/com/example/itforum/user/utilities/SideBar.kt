package com.example.itforum.utilities

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Composable
fun DrawerContent(

    onSelectNote: () -> Unit,
    onSelectChatAI: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxHeight()
            .width(260.dp) // Chiếm góc trái
            .background(Color(0xFFB0BEC5))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 1.dp, end = 12.dp, top = 22.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onCloseDrawer() }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Close Drawer"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tiện ích", fontSize = 23.sp,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Text(
                text = "Ghi chú",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSelectNote()
                        onCloseDrawer()
                    }
                    .padding(16.dp)
            )
            Divider()
            Text(
                text = "ChatAI",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSelectChatAI()
                        onCloseDrawer()
                    }
                    .padding(16.dp)
            )
        }
    }
}

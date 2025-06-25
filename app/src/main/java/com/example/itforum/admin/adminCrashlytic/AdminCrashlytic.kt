package com.example.itforum.admin.adminCrashlytic

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Date
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme


import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController






@Composable
fun CrashLogScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: CrashLogViewModel = viewModel()
) {
    val logs by viewModel.logs.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredLogs = logs.filter {
        searchQuery.isBlank() ||
                it.email.contains(searchQuery, ignoreCase = true) ||
                it.userId?.contains(searchQuery, ignoreCase = true) == true ||
                it.aiSummary.contains(searchQuery, ignoreCase = true) ||
                it.error.contains(searchQuery, ignoreCase = true)
    }

    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Tìm kiếm log...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            if (filteredLogs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚠ Không có log nào.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(filteredLogs) { log ->
                        var expanded by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("📧 Email: ${log.email}", fontWeight = FontWeight.Bold)
                                Text("🆔 UserID: ${log.userId ?: "Không có"}")
                                Text("⏰ Thời gian: ${Date(log.timestamp)}")
                                Text(
                                    text = "🤖 AI Summary: " + if (expanded) log.aiSummary else log.aiSummary.take(150) + "...",
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "❗ Error:\n" + if (expanded) log.error else log.error.take(150) + "...",
                                    color = Color.Red,
                                    fontSize = 13.sp
                                )
                                TextButton(onClick = { expanded = !expanded }) {
                                    Text(if (expanded) "Ẩn bớt" else "Xem thêm")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}





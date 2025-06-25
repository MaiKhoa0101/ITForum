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

import androidx.compose.material3.TopAppBarDefaults
import androidx.navigation.NavHostController






@Composable
fun CrashLogScreen(
    navHostController: NavHostController,
    viewModel: CrashLogViewModel = viewModel()
) {
    val logs by viewModel.logs.collectAsState()

    Scaffold(

    ) { innerPadding ->
        if (logs.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 50.dp)
            ) {
                Text("⚠ Không có log nào.")
            }
        } else {
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxSize()
                    .padding(16.dp),
            ) {
                items(logs) { log ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 50.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Email: ${log.email}")
                            Text("UserID: ${log.userId ?: "Không có"}")
                            Text("Time: ${Date(log.timestamp)}")
                            Text("Error:\n${log.error}", color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}





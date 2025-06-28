package com.example.itforum.admin.adminAnalytics

import android.content.SharedPreferences
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp

@Composable
fun AnalyticsScreen(navController: NavHostController,


                    viewModel: AnalyticsViewModel = viewModel()) {
    val events by viewModel.events.collectAsState(initial = emptyList()) // ✅ đảm bảo có giá trị ban đầu

    LaunchedEffect(Unit) {
        viewModel.fetchEvents()

    }

    Column(modifier = Modifier.padding(top = 50.dp)) {


        LazyColumn(modifier = Modifier.padding(top = 50.dp)) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Bảng thống kê thao tác người dùng",
                        fontSize = 20.sp
                    )
                }

            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Black)
                ) {
                    listOf("Màn hình", "Số thao tác", "Thời gian (giây)").forEach {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(0.5.dp, Color.Gray)
                                .padding(8.dp)
                        ) {
                            Text(text = it)
                        }
                    }
                }
            }

            items(events) { event ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray)
                ) {
                    listOf(
                        event.screen_name ?: "Không xác định",
                        event.view_count.toString(),
                        event.total_duration_seconds.toString()
                    ).forEach {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(0.5.dp, Color.Gray)
                                .padding(8.dp)
                        ) {
                            Text(text = it)
                        }
                    }
                }
            }
        }


    }

}

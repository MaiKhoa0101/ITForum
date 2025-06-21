package com.example.itforum.admin.adminAnalytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.itforum.user.Analytics.ScreenStat // Đảm bảo đúng path model

@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel = viewModel()) {
    val stats by viewModel.stats.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // ⚠️ Dùng h6 thay vì titleLarge nếu bạn dùng Material 2
        Text(
            text = "📊 Bảng thống kê màn hình",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Màn hình", fontWeight = FontWeight.Bold)
            Text("Lượt", fontWeight = FontWeight.Bold)
            Text("Thời gian (giây)", fontWeight = FontWeight.Bold)
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Dữ liệu từ server
        LazyColumn {
            items(stats) { stat ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stat.screen_name)
                    Text(stat.visit_count.toString())
                    Text(String.format("%.1f", stat.avg_duration))
                }
            }
        }
    }
}

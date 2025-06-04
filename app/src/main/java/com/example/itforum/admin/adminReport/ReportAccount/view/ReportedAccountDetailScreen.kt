package com.example.itforum.admin.adminReport.ReportAccount.view


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.itforum.admin.components.DetailItem
import com.example.itforum.admin.components.DetailScreenLayout
@Composable
fun ReportedAccountDetailScreen(
    reportedUserId: String,
    reportedUserName: String,
    email: String,
    phone: String,
    isBanned: Boolean,
    reporterName: String,
    reason: String,
    createdAt: String,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Người bị tố cáo: $reportedUserName")
        Text("Email: $email")
        Text("SĐT: $phone")
        Text("Bị khóa: ${if (isBanned) "Có" else "Không"}")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Người tố cáo: $reporterName")
        Text("Lý do: $reason")
        Text("Ngày tố cáo: $createdAt")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Quay lại")
        }
    }
}


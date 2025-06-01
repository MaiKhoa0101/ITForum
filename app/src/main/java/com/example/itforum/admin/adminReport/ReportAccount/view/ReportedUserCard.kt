package com.example.itforum.admin.adminReport.ReportAccount.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.itforum.admin.adminReport.ReportAccount.model.response.ReportedUser


@Composable
fun ReportedUserCard(user: ReportedUser, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tên: ${user.username}", style = MaterialTheme.typography.titleMedium)
            Text("Email: ${user.email}", style = MaterialTheme.typography.bodySmall)
            Text("Số lượt báo cáo: ${user.reportCount}", style = MaterialTheme.typography.bodySmall)
        }
    }
}


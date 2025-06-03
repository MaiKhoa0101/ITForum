package com.example.itforum.admin.adminReport.ReportPost.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.itforum.admin.components.DetailItem
import com.example.itforum.admin.components.DetailScreenLayout

@Composable
fun ReportedPostDetailScreen(
    postId: String,
    title: String,
    content: String,
    tags: List<String>,
    authorName: String,
    authorEmail: String,
    reporterName: String,
    reason: String,
    createdAt: String,
    onHidePost: () -> Unit = {},
    onLockPost: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    DetailScreenLayout(title = "Chi tiết bài viết bị tố cáo", onBack = onBack) {
        Text("Thông tin bài viết:", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        DetailItem("ID", postId)
        DetailItem("Tiêu đề", title)
        DetailItem("Nội dung", content)
        DetailItem("Tác giả", authorName)
        DetailItem("Email tác giả", authorEmail)
        DetailItem("Ngày đăng báo cáo", createdAt)
        DetailItem("Tags", tags.joinToString(", "))

        Spacer(modifier = Modifier.height(16.dp))
        Text("Người tố cáo: $reporterName", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))
        Text("Lý do tố cáo:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        DetailItem(label = "•", value = reason, divider = false)

        Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { println("Gỡ bài viết $postId") }) {
                    Text("Ẩn bài viết")
                }
                OutlinedButton(onClick = { println("Khóa bài viết $authorEmail") }) {
                    Text("Khóa bài viết")
                }
        }
    }
}

package com.example.itforum.admin.adminReport.ReportPost.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.itforum.admin.adminAccount.TableData
import com.example.itforum.admin.adminReport.ReportPost.convertReportedPostsToRows
import com.example.itforum.admin.adminReport.ReportPost.viewmodel.ReportedPostViewModel
import com.example.itforum.admin.components.AdminScreenLayout
import com.example.itforum.user.post.icontext

@Composable
fun ReportedPostScreen(
    viewModel: ReportedPostViewModel,
    navController: NavHostController
) {
    val posts by viewModel.posts.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadReportedPosts()
    }

    val menuOptions = listOf(
        icontext(Icons.Default.Visibility, "Xem chi tiết"),
        icontext(Icons.Default.Delete, "Xóa")
    )

    AdminScreenLayout(
        title = "Quản lý báo cáo bài viết",
        itemCount = posts.size
    ) { searchText, _, _ ->
        val filteredPosts = posts.filter {
            it.reason.contains(searchText, ignoreCase = true) ||
                    (it.reportedPostId?.contains(searchText, ignoreCase = true) ?:false)
        }

        if (error != null) {
            Text("Lỗi: $error", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        TableData(
            headers = listOf("ID", "Post ID", "User ID", "Lý do", "Ngày tạo"),
            rows = convertReportedPostsToRows(filteredPosts),
            menuOptions = menuOptions,
            onClickOption = { reportId ->
                navController.navigate("post_detail/$reportId")
            }
        )
    }
}
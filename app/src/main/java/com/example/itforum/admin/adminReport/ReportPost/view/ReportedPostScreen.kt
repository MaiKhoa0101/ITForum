package com.example.itforum.admin.adminReport.ReportPost.view

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.itforum.admin.adminReport.ReportPost.model.request.ReportedPost
import com.example.itforum.admin.adminReport.ReportPost.model.response.ReportedPostList
import com.example.itforum.admin.adminReport.ReportPost.viewmodel.ReportedPostViewModel
import com.example.itforum.admin.adminReport.ReportPost.viewmodel.ReportedPostViewModelFactory
import com.example.itforum.admin.components.AdminScreenLayout
import com.example.itforum.admin.components.TableData
import com.example.itforum.admin.components.convertToTableRows
import com.example.itforum.repository.ReportPostRepository
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.post.icontext

@Composable
fun ReportedPostScreen(navController: NavHostController,
                       sharedPreferences: SharedPreferences) {
    val viewModel: ReportedPostViewModel = viewModel(
        factory = ReportedPostViewModelFactory(
            ReportPostRepository(RetrofitInstance.reportPostService)
        )
    )
    val posts by viewModel.Posts.collectAsState()
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
            it.reportedPostId.contains(searchText, ignoreCase = true) ||
                    it.reportedPostTitle.contains(searchText, ignoreCase = true) ||
                    it.reason.contains(searchText, ignoreCase = true)
        }




        if (error != null) {
            Text("Lỗi: $error", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        TableData(
            headers = listOf("ID", "Post ID", "Tiêu đề", "Lý do", "Ngày tạo"),
            menuOptions = menuOptions,
            rows = convertToTableRows(filteredPosts),
            sharedPreferences = sharedPreferences,
            onClickOption = { reportId ->
                println("🟢 reportId được chọn: $reportId")
                navController.navigate("post_detail/$reportId")
            }

        )
    }
}
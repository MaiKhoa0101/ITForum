    package com.example.itforum.admin.adminReport.ReportPost.view

    import androidx.compose.foundation.layout.*
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import androidx.lifecycle.viewmodel.compose.viewModel
    import com.example.itforum.admin.adminReport.ReportPost.viewmodel.ReportedPostDetailViewModel
    import com.example.itforum.admin.adminReport.ReportPost.viewmodel.ReportedPostDetailViewModelFactory
    import com.example.itforum.admin.components.DetailItem
    import com.example.itforum.admin.components.DetailScreenLayout
    import com.example.itforum.repository.ReportPostRepository
    import com.example.itforum.retrofit.RetrofitInstance
    import kotlinx.coroutines.launch

    @Composable
    fun ReportedPostDetailScreen(
        reportId: String,
        onBack: () -> Unit
    ) {
        val viewModel: ReportedPostDetailViewModel = viewModel(
            factory = ReportedPostDetailViewModelFactory(
                ReportPostRepository(RetrofitInstance.reportPostService)
            )
        )

        LaunchedEffect(reportId) {
            viewModel.loadReportedPostById(reportId)
        }

        val data by viewModel.detail.collectAsState()

        if (data != null) {
            DetailScreenLayout(title = "Chi tiết bài viết bị tố cáo", onBack = onBack) {
                Text("Thông tin bài viết:", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))

                DetailItem("ID", data!!.reportedPost._id)
                DetailItem("Tiêu đề", data!!.reportedPost.title)
                DetailItem("Nội dung", data!!.reportedPost.content)
                DetailItem("Tác giả", data!!.reportedPost.userId)
                DetailItem("Ngày đăng", data!!.reportedPost.createdAt)
                DetailItem("Tags", data!!.reportedPost.tags.joinToString(", "))

                Spacer(modifier = Modifier.height(16.dp))
                Text("Người tố cáo: ${data!!.reporterUser.name}", style = MaterialTheme.typography.titleMedium)
                DetailItem("Email người tố cáo", data!!.reporterUser.email)

                Spacer(modifier = Modifier.height(16.dp))
                Text("Lý do tố cáo:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                DetailItem(label = "•", value = data!!.reason, divider = false)

                Spacer(modifier = Modifier.height(24.dp))
                val scope = rememberCoroutineScope()
                var hideMessage by remember { mutableStateOf<String?>(null) }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val response = RetrofitInstance.postService.hidePost(data!!.reportedPost._id)
                                    if (response.isSuccessful) {
                                        hideMessage = "✅ Bài viết đã được ẩn thành công!"
                                    } else {
                                        hideMessage = "❌ Lỗi ẩn bài viết: ${response.code()}"
                                    }
                                } catch (e: Exception) {
                                    hideMessage = "❌ Exception: ${e.message}"
                                }
                            }
                        }
                    ) {
                        Text("Ẩn bài viết")
                    }

                    OutlinedButton(onClick = {
                        println("🟧 Khóa tác giả ${data!!.reportedPost.userId}")
                    }) {
                        Text("Khóa bài viết")
                    }
                }

                hideMessage?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(it, color = MaterialTheme.colorScheme.primary)
                }

            }
        } else {
            Box(modifier = Modifier.padding(16.dp)) {
                Column {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Đang tải dữ liệu bài viết...")
                }
            }
        }
    }


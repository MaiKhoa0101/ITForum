package com.example.itforum.admin.adminReport.ReportPost.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.itforum.admin.adminReport.ReportPost.viewmodel.ReportedPostDetailViewModel
import com.example.itforum.admin.adminReport.ReportPost.viewmodel.ReportedPostDetailViewModelFactory
import com.example.itforum.admin.components.DetailItem
import com.example.itforum.admin.components.DetailScreenLayout
import com.example.itforum.repository.ReportPostRepository
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.post.VideoPlayer
import kotlinx.coroutines.launch
import android.net.Uri
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.core.net.toUri

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
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
                    .padding(bottom = 16.dp)
            ) {

                Text("Thông tin bài viết:", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))

                DetailItem("ID", data!!.reportedPost._id)
                DetailItem("Tiêu đề", data!!.reportedPost.title)
                DetailItem("Nội dung", data!!.reportedPost.content)
                DetailItem("Tác giả", data!!.reportedPost.userId)
                DetailItem("Ngày đăng", data!!.reportedPost.createdAt)
                DetailItem("Tags", data!!.reportedPost.tags.joinToString(", "))
                if (!data!!.reportedPost.imageUrls.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Hình ảnh:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(data!!.reportedPost.imageUrls.size) { index ->
                            AsyncImage(
                                model = data!!.reportedPost.imageUrls[index],
                                contentDescription = null,
                                modifier = Modifier
                                    .width(250.dp)
                                    .height(200.dp)
                            )
                        }
                    }
                }

                if (!data!!.reportedPost.videoUrls.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Video:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(data!!.reportedPost.videoUrls.size) { index ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "📹 Video ${index + 1}",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                VideoPlayer(
                                    uri = data!!.reportedPost.videoUrls[index].toUri()
                                )
                                println(data!!.reportedPost.videoUrls[index])
                            }
                        }
                    }
                }



                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Người tố cáo: ${data!!.reporterUser.name}",
                    style = MaterialTheme.typography.titleMedium
                )
                DetailItem("Email người tố cáo", data!!.reporterUser.email)

                Spacer(modifier = Modifier.height(16.dp))
                Text("Lý do tố cáo:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                DetailItem(label = "•", value = data!!.reason, divider = false)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Kết quả phân tích AI:", style = MaterialTheme.typography.titleMedium)

                val ai = data!!.aiAnalysis
                if (ai != null) {
                    DetailItem("Mức độ vi phạm (%)", "${ai.violationPercentage}")
                    DetailItem("Giải thích", ai.reason)
                    DetailItem("Có nên khóa bài viết?", if (ai.shouldBan) "✅ Có" else "❌ Không")
                } else {
                    Text("⏳ Đang phân tích AI hoặc chưa có kết quả.")
                }

                Spacer(modifier = Modifier.height(24.dp))
                val scope = rememberCoroutineScope()
                var hideMessage by remember { mutableStateOf<String?>(null) }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val response =
                                        RetrofitInstance.postService.hidePost(data!!.reportedPost._id)
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

                }

                hideMessage?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(it, color = MaterialTheme.colorScheme.primary)
                }

            }
        } }
    else {
            Box(modifier = Modifier.padding(16.dp)) {
                Column {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Đang tải dữ liệu bài viết...")
                }
            }
        }
    }


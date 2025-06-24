package com.example.itforum.admin.adminPost

import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.itforum.admin.components.DetailScreenLayout
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.post.VideoPlayer
import com.example.itforum.user.post.viewmodel.PostViewModel
import kotlinx.coroutines.launch

@Composable
fun PostDetailScreen(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    postId: String,
) {
    val postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(navHostController, sharedPreferences) }
    })

    LaunchedEffect(postId) {
        postViewModel.getPostById(postId)
    }

    val post by postViewModel.post.collectAsState()

    if (post != null) {
        DetailScreenLayout(title = "Chi tiết bài viết", onBack = { navHostController.popBackStack() }) {
            Text("Thông tin bài viết:", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            post!!.id?.let { DetailItem("ID", it) }
            post!!.title?.let { DetailItem("Tiêu đề", it) }
            post!!.content?.let { DetailItem("Nội dung", it) }
            post!!.userId?.let { DetailItem("Tác giả", it) }
            post!!.createdAt?.let { DetailItem("Ngày đăng", it) }
            post!!.tags?.let { DetailItem("Tags", it.joinToString(", ")) }
            if (!post!!.imageUrls.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Hình ảnh:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    post!!.imageUrls?.let {
                        items(it.size) { index ->
                            AsyncImage(
                                model = post!!.imageUrls!![index],
                                contentDescription = null,
                                modifier = Modifier
                                    .width(250.dp)
                                    .height(200.dp)
                            )
                        }
                    }
                }
            }

            if (!post!!.videoUrls.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Video:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    post!!.videoUrls?.let {
                        items(it.size) { index ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "📹 Video ${index + 1}",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                VideoPlayer(
                                    uri = post!!.videoUrls!![index].toUri()
                                )
                                println(post!!.videoUrls!![index])
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            val scope = rememberCoroutineScope()
            var hideMessage by remember { mutableStateOf<String?>(null) }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val response = post!!.id?.let {
                                    RetrofitInstance.postService.hidePost(
                                        it
                                    )
                                }
                                if (response != null) {
                                    if (response.isSuccessful) {
                                        hideMessage = "✅ Bài viết đã được ẩn thành công!"
                                    } else {
                                        hideMessage = "❌ Lỗi ẩn bài viết: ${response.code()}"
                                    }
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
                    println("🟧 Khóa tác giả ${post!!.userId}")
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

@Composable
private fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = Color(0xFF555555),
            fontSize = 16.sp,
            modifier = Modifier.width(130.dp)
        )
        Text(text = value, fontSize = 16.sp, color = Color.Black)
    }
}

@Composable
fun DropdownMenuBox(
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedOption)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { label ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onOptionSelected(label)
                        expanded = false
                    }
                )
            }
        }
    }
}
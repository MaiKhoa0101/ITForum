package com.example.itforum.admin.adminPost

import android.R.attr.fontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itforum.admin.postManagement.Post

@Composable
fun PostDetailScreen(
    post: Post,
    onBack: () -> Unit = {},
    onChangeStatus: (Post, String) -> Unit = { _, _ -> },
    onSendWarning: (Post) -> Unit = {}
) {
    var selectedStatus by remember { mutableStateOf(post.status) }
    var tempStatus by remember { mutableStateOf(post.status) }
    val statusOptions = listOf("Đang hoạt động", "Đã ẩn", "Tố cáo")


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(69.dp)
                .background(Color(0xFF00AEFF)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 25.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Chi tiết bài viết", color = Color.White, fontSize = 18.sp,fontWeight= FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            DetailItem("ID:", post.id.toString())
            Divider(color = Color.LightGray, thickness = 1.dp)
            DetailItem("Tác giả:", post.author)
            Divider(color = Color.LightGray, thickness = 1.dp)
            DetailItem("Thời gian đăng:", post.time)
            Divider(color = Color.LightGray, thickness = 1.dp)
            DetailItem("Trạng thái:", selectedStatus)
            Divider(color = Color.LightGray, thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Tùy chỉnh trạng thái:", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            DropdownMenuBox(
                selectedOption = tempStatus,
                options = statusOptions
            ) {
                tempStatus = it
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    selectedStatus = tempStatus
                    onChangeStatus(post, tempStatus)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AEFF))
            ) {
                Text("Áp dụng", color = Color.White)
            }


            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { onSendWarning(post) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))
                ) {
                    Text("Gửi cảnh báo", color = Color.White)
                }

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

fun samplePost() = Post(
    id = 1,
    author = "Khang đẹp trai",
    time = "10:00, 23/05/2025",
    status = "Đang hoạt động"
)

@Preview(showBackground = true)
@Composable
fun PreviewPostDetailScreen() {
    PostDetailScreen(post = samplePost())
}

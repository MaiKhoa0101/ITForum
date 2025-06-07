package com.example.itforum.admin.postManagement

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun PostManagementScreen(navHostController: NavHostController,posts: List<Post>) {
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    var expandedFilter by remember { mutableStateOf(false) }
    var filteredPosts by remember { mutableStateOf(posts) }
    var expandedIndex by remember { mutableStateOf(-1) }

    val filterOptions = listOf("Tất cả", "Đang hoạt động", "Đã ẩn", "Tố cáo")

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Box(modifier = Modifier.fillMaxWidth().height(70.dp).background(Color(0xFF00AEFF)), contentAlignment = Alignment.CenterStart) {
            Text(
                text = "Quản lí bài viết",
                modifier = Modifier.padding(start = 20.dp),
                color = Color.White,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    filteredPosts = posts.filter { post ->
                        post.author.contains(it, true) || post.time.contains(it, true)
                    }.filter { post ->
                        selectedFilter == "Tất cả" || post.status == selectedFilter
                    }
                },
                placeholder = { Text("Search") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box {
                Button(onClick = { expandedFilter = true }) {
                    Text("Lọc: $selectedFilter")
                }
                DropdownMenu(expanded = expandedFilter, onDismissRequest = { expandedFilter = false }) {
                    filterOptions.forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = {
                            selectedFilter = option
                            expandedFilter = false
                            filteredPosts = posts.filter {
                                (searchText.isBlank() || it.author.contains(searchText, true) || it.time.contains(searchText, true)) &&
                                        (selectedFilter == "Tất cả" || it.status == selectedFilter)
                            }
                        })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val headers = listOf("ID", "Tác giả", "Thời gian đăng", "Trạng thái", "Tuỳ chọn")
        val columnWidths = listOf(50.dp, 120.dp, 140.dp, 120.dp, 100.dp)
        val scrollState = rememberScrollState()

        LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
            item {
                Row(modifier = Modifier.horizontalScroll(scrollState).background(Color(0xFF29B6F6))) {
                    headers.forEachIndexed { index, title ->
                        Box(modifier = Modifier.width(columnWidths[index]).padding(8.dp)) {
                            Text(title, color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }

            itemsIndexed(filteredPosts) { index, post ->
                val bgColor = if (index % 2 == 0) Color.White else Color(0xFFF0F0F0)
                Row(modifier = Modifier.horizontalScroll(scrollState).background(bgColor), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.width(columnWidths[0]).padding(8.dp)) {
                        Text(post.id.toString())
                    }
                    Box(modifier = Modifier.width(columnWidths[1]).padding(8.dp)) {
                        Text(post.author)
                    }
                    Box(modifier = Modifier.width(columnWidths[2]).padding(8.dp)) {
                        Text(post.time)
                    }
                    Box(modifier = Modifier.width(columnWidths[3]).padding(8.dp)) {
                        Text(
                            post.status,
                            color = when (post.status) {
                                "Đang hoạt động" -> Color(0xFF43A047)
                                "Đã ẩn" -> Color.Gray
                                "Tố cáo" -> Color.Red
                                else -> Color.Black
                            }
                        )
                    }
                    Box(modifier = Modifier.width(columnWidths[4]).padding(8.dp), contentAlignment = Alignment.Center) {
                        IconButton(onClick = { expandedIndex = index }) {
                            Icon(Icons.Default.MoreHoriz, contentDescription = null)
                        }
                        DropdownMenu(expanded = expandedIndex == index, onDismissRequest = { expandedIndex = -1 }) {
                            DropdownMenuItem(text = { Text("Xem chi tiết", color = Color(0xFF00AEFF)) }, onClick = {
                                expandedIndex = -1
                                // handle view detail
                            })
                            DropdownMenuItem(text = { Text("Xoá", color = Color.Red) }, onClick = {
                                expandedIndex = -1
                                // handle delete
                            })
                        }
                    }
                }
                Divider()
            }
        }
    }
}

data class Post(
    val id: Int,
    val author: String,
    val time: String,
    val status: String
)

fun samplePosts(): List<Post> = listOf(
    Post(1, "Khang đẹp trai", "30/4/2025", "Đang hoạt động"),
    Post(2, "Đạt mot mat", "30/4/2025", "Đã ẩn"),
    Post(3, "Khoa đẹp gái", "30/4/2025", "Tố cáo")
)

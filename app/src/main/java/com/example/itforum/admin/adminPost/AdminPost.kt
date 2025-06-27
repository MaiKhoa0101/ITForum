package com.example.itforum.admin.postManagement

import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.itforum.admin.components.AdminScreenLayout
import com.example.itforum.admin.components.TableData
import com.example.itforum.admin.components.TableRowConvertible
import com.example.itforum.admin.components.convertToTableRows
import com.example.itforum.user.modelData.response.PostResponse
import com.example.itforum.user.post.icontext
import com.example.itforum.user.post.viewmodel.PostViewModel
import java.time.Instant

@Composable
fun PostManagementScreen(
    modifier: Modifier,
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences
) {
    val postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(sharedPreferences) }
    })
    LaunchedEffect(Unit) {
        postViewModel.getAllPost()
    }
    val listPost by postViewModel.listPost.collectAsState()
    val menuOptions = listOf(
        icontext(Icons.Default.Edit,"Xem chi tiết", { postId ->
            navHostController.navigate("post_detail/$postId")
        }),
        icontext(Icons.Default.Delete,"Xóa")
    )
    val filterOptions = mapOf(
        "Trạng thái" to listOf("Ẩn", "Hiển thị"),
        "Ngày tạo" to listOf("Mới nhất", "Cũ nhất"),
        "Phạm vi" to listOf("Công khai", "Riêng tư")
    )
    AdminScreenLayout(
        title = "Quản lý bài viết",
        itemCount = listPost.size,
        modifier = modifier,
        searchTable = { searchText ->
            val dataFiltered = listPost.filter { item ->
                val isPublishedText = if (item.isPublished == "public") "công khai" else "chỉ mình tôi"
                val isHiddenText = if (item.isHidden == true) "ẩn" else "hiện"
                searchText.isBlank() || listOf(
                    item.id,
                    item.title,
                    item.content,
                    item.tags?.joinToString(", "),
                    item.createdAt,
                    isPublishedText,
                    isHiddenText
                ).any { it?.contains(searchText, ignoreCase = true) ?: true }
            }
            return@AdminScreenLayout dataFiltered
        },
        filterOptions = filterOptions,
        filterField = { field, value, data ->
            data as List<PostResponse>
            val dataFiltered = when (field) {
                "Trạng thái" -> {
                    when (value) {
                        "Ẩn" -> data.filter { it.isHidden == true }
                        "Hiển thị" -> data.filter { it.isHidden == false }
                        else -> data
                    }
                }
                "Ngày tạo" -> {
                    when (value) {
                        "Mới nhất" -> data.sortedByDescending { Instant.parse(it.createdAt) }
                        "Cũ nhất" -> data.sortedBy { Instant.parse(it.createdAt) }
                        else -> data
                    }
                }
                "Phạm vi" -> {
                    when (value) {
                        "Công khai" -> data.filter { it.isPublished == "public" }
                        "Riêng tư" -> data.filter { it.isPublished != "public" }
                        else -> data
                    }
                }
                else -> data
            }
            return@AdminScreenLayout dataFiltered
        },
        table = { dataFiltered ->
            TableData(
                headers = listOf(
                    "ID",
                    "Người dùng",
                    "Tiêu đề",
                    "Nội dung",
                    "Tag",
                    "Phạm vi",
                    "Ngày tạo",
                    "Trạng thái",
                    "Tùy chỉnh"
                ),
                rows = convertToTableRows(dataFiltered as List<TableRowConvertible>),
                menuOptions = menuOptions,
                sharedPreferences = sharedPreferences
            )
        }
    )

}
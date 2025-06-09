package com.example.itforum.admin.adminNews

import android.content.SharedPreferences
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.itforum.admin.components.AdminScreenLayout
import com.example.itforum.admin.components.TableData
import com.example.itforum.admin.components.convertToTableRows
import com.example.itforum.user.news.viewmodel.NewsViewModel
import com.example.itforum.user.post.icontext

@Composable
fun ManagementNewsScreen (
    navController: NavHostController,
    sharedPreferences: SharedPreferences
) {
    val newsViewModel: NewsViewModel = viewModel(factory = viewModelFactory {
        initializer { NewsViewModel(sharedPreferences) }
    })
    LaunchedEffect(Unit) {
        newsViewModel.getNews()
    }
    val listNews by newsViewModel.listNews.collectAsState()
    val menuOptions = listOf(
        icontext(Icons.Default.Edit,"Xem chi tiết",{ newsId ->
            navController.navigate("detail_news/$newsId")
        })
    )
    listNews?.let {
        AdminScreenLayout(
            title = "Quản lý báo cáo người dùng",
            itemCount = it.size,
            addComposed = {
                Button(
                    onClick = {
                        navController.navigate("create_news")
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF2196F3),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Thêm mới",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(3.dp),
                    )
                }
            }
        ) { searchText, _, _ ->
            val filteredUsers = listNews!!.filter {
                it.adminId.contains(searchText, ignoreCase = true) ||
                it.createdAt.contains(searchText, ignoreCase = true) ||
                it.title.contains(searchText, ignoreCase = true) ||
                it.content.contains(searchText, ignoreCase = true)
            }
            TableData(
                headers = listOf("ID", "Người dùng", "Tiêu đề", "Nội dung", "Thời gian", "Tùy chỉnh"),
                rows = convertToTableRows(filteredUsers),
                menuOptions = menuOptions,
                sharedPreferences = sharedPreferences,
            )
        }
    }
}
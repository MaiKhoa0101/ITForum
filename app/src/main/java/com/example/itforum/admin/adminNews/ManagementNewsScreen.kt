package com.example.itforum.admin.adminNews

import android.content.SharedPreferences
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.room.Room
import com.example.itforum.admin.components.AdminScreenLayout
import com.example.itforum.admin.components.TableData
import com.example.itforum.admin.components.TableRowConvertible
import com.example.itforum.admin.components.convertToTableRows
import com.example.itforum.user.modelData.response.News
import com.example.itforum.user.news.NewsDatabase
import com.example.itforum.user.news.viewmodel.NewsViewModel
import com.example.itforum.user.news.viewmodel.NewsViewModelFactory
import com.example.itforum.user.post.icontext
import java.time.Instant

@Composable
fun ManagementNewsScreen (
    navController: NavHostController,
    sharedPreferences: SharedPreferences
) {
    val context = LocalContext.current

    val db = Room.databaseBuilder(
        context,
        NewsDatabase::class.java,
        "news-db"
    ).build()

    val newsDao = db.newsDao()
    val newsViewModel: NewsViewModel = viewModel(
        factory = NewsViewModelFactory(newsDao, sharedPreferences)
    )

    LaunchedEffect(Unit) {
        newsViewModel.getNews()
    }
    val listNews by newsViewModel.listNews.collectAsState()
    val menuOptions = listOf(
        icontext(Icons.Default.Edit,"Xem chi tiết",{ newsId ->
            navController.navigate("detail_news/$newsId")
        }),
        icontext(Icons.Default.Delete,"Xóa",{ newsId ->
            newsViewModel.DeleteNews(newsId)
        })
    )
    val filterOptions = mapOf(
        "Thời gian" to listOf("Mới nhất", "Cũ nhất")
    )
    listNews?.let {
        AdminScreenLayout(
            title = "Quản lý tin tức",
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
            },
            searchTable = { searchText ->
                val dataFiltered = listNews!!.filter { item ->
                    searchText.isBlank() || listOf(
                        item.adminId,
                        item.createdAt,
                        item.title,
                        item.content
                    ).any { it.contains(searchText, ignoreCase = true) }
                }
                return@AdminScreenLayout dataFiltered
            },
            filterOptions = filterOptions,
            filterField = { field, value, data ->
                data as List<News>
                val dataFiltered = when (field) {
                    "Thời gian" -> {
                        when (value) {
                            "Mới nhất" -> data.sortedByDescending { Instant.parse(it.createdAt) }
                            "Cũ nhất" -> data.sortedBy { Instant.parse(it.createdAt) }
                            else -> data
                        }
                    }
                    else -> data
                }
                return@AdminScreenLayout dataFiltered
            },
            table = {dataFiltered ->
                TableData(
                    headers = listOf("ID", "Người dùng", "Tiêu đề", "Nội dung", "Thời gian", "Tùy chỉnh"),
                    rows = convertToTableRows(dataFiltered as List<TableRowConvertible>),
                    menuOptions = menuOptions,
                    sharedPreferences = sharedPreferences,
                )
            }
        )
    }
}
package com.example.itforum.admin.adminNews

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.room.Room
import com.example.itforum.admin.adminComplaint.viewmodel.ComplaintViewModel
import com.example.itforum.user.complaint.AddImage
import com.example.itforum.user.complaint.SuccessDialog
import com.example.itforum.user.complaint.TitleChild
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.modelData.request.ComplaintRequest
import com.example.itforum.user.modelData.request.NewsRequest
import com.example.itforum.user.news.NewsDatabase
import com.example.itforum.user.news.viewmodel.NewsViewModel
import com.example.itforum.user.news.viewmodel.NewsViewModelFactory
import com.example.itforum.user.post.IconWithText
import com.example.itforum.user.post.TopPost
import com.example.itforum.user.post.WritePost
import com.example.itforum.user.profile.viewmodel.UserViewModel

@Composable
fun CreateNewsScreen(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences
){
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val context = LocalContext.current
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var img by remember  { mutableStateOf<Uri?>(null) }

        var userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
            initializer { UserViewModel(sharedPreferences) }
        })
        val db = Room.databaseBuilder(
            context,
            NewsDatabase::class.java,
            "news-db"
        ).build()

        val newsDao = db.newsDao()
        val newsViewModel: NewsViewModel = viewModel(
            factory = NewsViewModelFactory(newsDao, sharedPreferences)
        )

        val userInfo by userViewModel.user.collectAsState()
        val uiState by newsViewModel.uiStateCreate.collectAsState()
        var showSuccessDialog by remember { mutableStateOf(false) }
        var enable by remember { mutableStateOf<Boolean>(true) }
        LaunchedEffect(uiState) {
            println("UI State duoc thay doi")
            if (uiState is UiState.Success) {
                println("uiState là success")
                showSuccessDialog = true
            }else if(uiState is UiState.Loading){
                enable = false
            }
        }

        LaunchedEffect(Unit) {
            userViewModel.getUser()
        }
        // UI hiển thị
        if (showSuccessDialog) {
            SuccessDialog(
                message = "Tạo tin tức thành công!",
                onDismiss = {
                    showSuccessDialog = false
                    navHostController.navigate("NewsManager")
                }
            )
        }
        LazyColumn (
            modifier = Modifier.fillMaxSize()
        ) {
            stickyHeader { TopPost("Tạo tin tức", "Thêm",navHostController, enable, uiState) {
                newsViewModel.createNews(
                    NewsRequest(
                        adminId = userInfo?.id ?: "",
                        title = title,
                        content = content,
                        img = img
                    ),
                    context
                )
            }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                ) {
                    userInfo?.let { IconWithText(avatar = it.avatar, name = it.name) }
                    TitleChild(){ title=it }
                    WritePost(){input ->
                        content = input
                    }
                    AddImage(){img=it}
                }
            }
        }
    }
}
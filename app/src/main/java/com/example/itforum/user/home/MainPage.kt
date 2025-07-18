package com.example.itforum.user.home

import android.content.SharedPreferences
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.room.Room
import com.example.itforum.R
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.ReportPost.view.CreateReportPostScreen
import com.example.itforum.user.modelData.request.GetPostRequest
import com.example.itforum.user.news.AdvancedMarqueeTextList
import com.example.itforum.user.news.NewsDatabase
import com.example.itforum.user.news.viewmodel.NewsViewModel
import com.example.itforum.user.news.viewmodel.NewsViewModelFactory
import com.example.itforum.user.permission.RequestPermissionUI
import com.example.itforum.user.permission.checkPermission
import com.example.itforum.user.post.PostListScreen
import com.example.itforum.user.post.viewmodel.CommentViewModel
import com.example.itforum.user.post.viewmodel.PostViewModel
import com.example.itforum.user.skeleton.SkeletonPost

@Composable
fun HomePage(
    navHostController: NavHostController,
    modifier: Modifier,
    sharePreferences: SharedPreferences,
    postViewModel: PostViewModel,
    commentViewModel: CommentViewModel,
    listState: LazyListState,
    onToggleBars: () -> Unit,
    ){
    val context = LocalContext.current
    val db = Room.databaseBuilder(
        context,
        NewsDatabase::class.java,
        "news-db"
    )
        .fallbackToDestructiveMigration()
        .build()

    val newsDao = db.newsDao()
    val newsViewModel: NewsViewModel = viewModel(
        factory = NewsViewModelFactory(newsDao, sharePreferences)
    )
    val progress by postViewModel.uploadProgress.collectAsState()
    val uiStateCreate by postViewModel.uiStateCreate.collectAsState()

    LaunchedEffect(Unit) {
        newsViewModel.getNews()
    }
    val listNews by newsViewModel.listNews.collectAsState()
    LaunchedEffect(uiStateCreate) {
        println("UI State duoc thay doi")
        if (uiStateCreate is UiState.Success) {
            println("uiState là success")
        }
    }
    Column(
        modifier=modifier.fillMaxSize()
//            .pointerInput(Unit) {
//                detectTapGestures(onTap = { onToggleBars() })
//            }
        ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Column(
            modifier = Modifier
                .shadow(10.dp, shape = MaterialTheme.shapes.medium)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            if (listNews != null) {
                Text(
                    text = "Tin tức",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                AdvancedMarqueeTextList(
                    listNews!!, navHostController,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                        .height(40.dp)
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            PostListScreen(
                sharePreferences,
                navHostController,
                GetPostRequest(page = 1),
                postViewModel = postViewModel,
                commentViewModel = commentViewModel,
                listState = listState,
                onToggleBars = onToggleBars
            );
            Row (
                modifier = Modifier.padding(bottom = 50.dp)
                    .align(Alignment.BottomEnd)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    )
                    .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (progress in 0f..1f && progress != 0f) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Đang đăng bài: ${(progress * 100).toInt()}%",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SkeletonPost()
                    }
                } else if (uiStateCreate is UiState.Success) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_success),
                            contentDescription = "Success",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Đăng bài thành công",
                            color = Color.Green,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }


            }
        }
    }
    RequestPermissionUI()
}
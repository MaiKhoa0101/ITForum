package com.example.itforum.user.news

import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.room.Room
import coil.compose.AsyncImage
import com.example.itforum.R
import com.example.itforum.ui.theme.ITForumTheme
import com.example.itforum.user.news.viewmodel.NewsViewModel
import com.example.itforum.user.news.viewmodel.NewsViewModelFactory
import com.example.itforum.user.post.AvatarNameDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailNewsPage(
    newsId: String,
    navHostController: NavHostController,
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
        newsViewModel.getByIdNews(newsId)
    }
    val news by newsViewModel.news.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 100.dp),
                    ) {
                        Text(
                            text = "Tin tức",
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                navigationIcon = {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                navHostController.popBackStack()
                            }
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if(news?.img != null) {
                        AsyncImage(
                            model = news!!.img,
                            contentDescription = "",
                            modifier = Modifier.size(200.dp),
                            contentScale = ContentScale.Inside,
                        )
                    }
                }
            }
            item {
                Text(
                    text = news?.title?: "Lỗi ko lấy được tin tức",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
                )
            }
            item{
                AvatarNameDetail(
                    avatar = "",
                    name = "Nguyễn Thành Đạt",
                    time = news?.createdAt?:"2050-06-01T08:14:16.547+00:00"
                )
            }
            item{
                Text(
                    text = news?.content?: "Lỗi ko lấy được tin tức",
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
                )
            }
        }
    }
}

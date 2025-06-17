package com.example.itforum.user.home

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.NavController
import androidx.room.Room
import com.example.itforum.user.modelData.request.GetPostRequest
import com.example.itforum.user.news.NewsDatabase
import com.example.itforum.user.news.viewmodel.NewsViewModel
import com.example.itforum.user.news.viewmodel.NewsViewModelFactory
import com.example.itforum.user.post.AdvancedMarqueeTextList
import com.example.itforum.user.post.PostListScreen

@Composable
fun HomePage(
    navHostController: NavHostController,
    modifier: Modifier,
    sharePreferences: SharedPreferences
){
    Column(
        modifier=modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
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

        LaunchedEffect(Unit) {
            newsViewModel.getNews()
        }
        val listNews by newsViewModel.listNews.collectAsState()

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
                    color = MaterialTheme.colorScheme.onSecondaryContainer
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
        PostListScreen(sharePreferences, navHostController, GetPostRequest(page = 1));
    }
}
package com.example.itforum.user.home

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.NavController
import com.example.itforum.user.modelData.request.GetPostRequest
import com.example.itforum.user.news.viewmodel.NewsViewModel
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
        val newsViewModel: NewsViewModel = viewModel(factory = viewModelFactory {
            initializer { NewsViewModel(sharePreferences) }
        })

        LaunchedEffect(Unit) {
            newsViewModel.getNews()
        }
        val listNews by newsViewModel.listNews.collectAsState()

        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            if (listNews != null) {
                Text(
                    text = "Tin tức",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                AdvancedMarqueeTextList(
                    listNews!!, navHostController,
                    modifier = Modifier
                        .padding(horizontal = 30.dp)
                        .width(300.dp)
                        .height(40.dp)
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 50.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Black)
                )
            }
        }
        PostListScreen(sharePreferences, navHostController, GetPostRequest(page = 1));
    }
}
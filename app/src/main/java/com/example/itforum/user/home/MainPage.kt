package com.example.itforum.user.home

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavController
import com.example.itforum.user.modelData.request.GetPostRequest
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
        PostListScreen(sharePreferences, navHostController, GetPostRequest(page = 1));
    }
}
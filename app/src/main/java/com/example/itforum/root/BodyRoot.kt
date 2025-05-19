package com.example.itforum.root

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.itforum.home.HomePage
import com.example.itforum.notification.NotificationPage
import com.example.itforum.post.CreatePostPage
import com.example.itforum.post.DetailPostPage
import com.example.itforum.post.ListLikePage
import com.example.itforum.profile.UserProfile
import com.example.itforum.tool.ToolPage
@Composable
fun BodyRoot(navHostController: NavHostController,modifier: Modifier){
    NavHost(navHostController, startDestination = "home") {
        composable ("home") {
            HomePage(modifier)
        }
        composable ("notification") {
            NotificationPage(modifier)
        }
        composable ("tool") {
            ToolPage(modifier)
        }
        composable ("personal") {
            UserProfile(modifier)
        }
        composable("create_post") {
            CreatePostPage(modifier, navHostController)
        }
        composable("detail_post"){
            DetailPostPage(navHostController)
        }
        composable("listlike") {
            ListLikePage(navHostController)
        }
    }
}
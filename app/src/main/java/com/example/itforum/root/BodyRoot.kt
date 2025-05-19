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
import com.example.itforum.profile.EditProfile
import com.example.itforum.profile.OtherProfile
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
            UserProfile(modifier, navHostController)
        }
        composable ("otherprofile") {
            OtherProfile(modifier)
        }
        composable ("editprofile") {
            EditProfile(navHostController)
        }

    }
}
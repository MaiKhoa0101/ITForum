package com.example.itforum.admin.AdminRoot

import android.content.SharedPreferences
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun AdminRoot(navHostController: NavHostController, sharePreferences: SharedPreferences, accessToken: String) {
    Text("admin")
}
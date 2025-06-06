package com.example.itforum.admin.AdminRoot

import android.content.SharedPreferences
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun AdminRoot(navHostController: NavHostController, sharePreferences: SharedPreferences, accessToken: String) {
    Button(onClick = {navHostController.navigate("manager_complaint")}) { Text("Quản lý khiếu nại") }
}
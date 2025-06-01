package com.example.itforum.admin.AdminRoot

import android.content.SharedPreferences
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.itforum.ui.theme.ITForumTheme


@Composable
fun AdminRoot(sharedPreferences: SharedPreferences){
    val navHostController= rememberNavController()
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute=navBackStackEntry?.destination?.route
    ITForumTheme (darkTheme = false){
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                AdminTopBar(currentRoute)
            },
            bottomBar = {
                AdminBottomBar(navHostController,currentRoute)
            }
        ) {innerPadding->
            AdminBodyRoot(
                modifier= Modifier.padding(innerPadding),
                navHostController=navHostController,
                sharedPreferences=sharedPreferences
            )

        }
    }
}
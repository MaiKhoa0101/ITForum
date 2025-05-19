package com.example.itforum.root

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Scaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.itforum.ui.theme.ITForumTheme
import androidx.navigation.compose.rememberNavController
import com.example.itforum.post.CreatePostPage
import com.example.itforum.post.DetailPostPage
import com.example.itforum.post.abc
import kotlin.collections.contains

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Root()
        }
    }
}


@Composable
fun Root(){
    var darkTheme by rememberSaveable { mutableStateOf(false) }
    val navHostController = rememberNavController()
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showTopBars = currentRoute in listOf("home")
    val showFootBars = currentRoute in listOf("home", "tool", "notification", "personal")
    ITForumTheme (darkTheme = darkTheme)
    {
        println(darkTheme)
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (showTopBars) {
                    TopBarRoot(onToggleTheme = { darkTheme = !darkTheme })
                }
            },
            bottomBar = {
                if (showFootBars) {
                    FootBarRoot(currentRoute=currentRoute,navHostController = navHostController)
                }
            }
        ) { innerPadding ->
            BodyRoot(navHostController,modifier = Modifier.padding(innerPadding))
        }
    }
}

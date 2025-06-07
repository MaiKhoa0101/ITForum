package com.example.itforum.user.root

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.itforum.ui.theme.ITForumTheme
import androidx.navigation.compose.rememberNavController
import kotlin.collections.contains

import com.example.itforum.utilities.DrawerContent
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.itforum.admin.AdminRoot.AdminRoot
import com.example.itforum.user.login.LoginScreen
import kotlinx.coroutines.launch
//import com.example.itforum.admin.
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        setContent {
            ITForumTheme {
                Root(sharedPreferences)

            }
        }
    }}



@Composable
fun Root(sharedPreferences:SharedPreferences) {
    var darkTheme by rememberSaveable { mutableStateOf(false) }
    val navHostController = rememberNavController()
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showTopBars = currentRoute in listOf("home","bookmark")
    val showFootBars = currentRoute in listOf("home", "searchscreen", "notification", "personal","bookmark")
    //thay doi ơ day
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            Log.d("FCM", "Token from Composable: $token")
        } catch (e: Exception) {
            Log.e("FCM", "Token fetch failed", e)
        }
    }
    ITForumTheme(darkTheme = darkTheme)
    {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
                    onCloseDrawer = {
                        coroutineScope.launch { drawerState.close() }
                    },
                    onSelectChatAI = {
                        coroutineScope.launch { drawerState.close() }
                        navHostController.navigate("chat")
                    },
                    onSelectNote = {
                        coroutineScope.launch { drawerState.close() }
                        navHostController.navigate("note")
                    }
                )
            },
            scrimColor = Color.Transparent
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    if (showTopBars) {
                        TopBarRoot(
                            navHostController,
                            onMenuClick = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            })
                    }
                },
                bottomBar = {
                    if (showFootBars) {
                        FootBarRoot(
                            currentRoute = currentRoute,
                            navHostController = navHostController
                        )
                    }
                }
            ) { innerPadding ->
                BodyRoot(
                    sharedPreferences,
                    navHostController,
                    modifier = Modifier.padding(innerPadding),
                    onToggleTheme = {
                        darkTheme = !darkTheme
                    },
                    darkTheme = darkTheme
                )
            }
        }
    }
}
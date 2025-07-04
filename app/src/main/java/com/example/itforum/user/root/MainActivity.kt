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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.itforum.user.login.LoginScreen
import kotlinx.coroutines.launch
//import com.example.itforum.admin.
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.itforum.admin.adminAnalytics.AnalyticsScreen

import com.example.itforum.admin.adminCrashlytic.CrashLogScreen



import com.example.itforum.admin.modeldata.sidebarItems
import com.example.itforum.admin.modeldata.sidebarUserItems
import com.example.itforum.user.FilterWords.ToastHelper
import com.google.firebase.messaging.FirebaseMessaging
import com.example.itforum.user.ReportAccount.view.CreateReportAccountScreen
import com.example.itforum.user.ReportPost.view.CreateReportPostScreen
import com.example.itforum.user.complaint.SuccessDialog
import com.example.itforum.user.userProfile.viewmodel.UserViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.tasks.await


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        // Khởi tạo Firebase Analytics
        val firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // Gửi sự kiện screen_view
        val screenBundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "RootScreen")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, screenBundle)
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Subscribed to all_users topic")
                } else {
                    Log.e("FCM", "Topic subscription failed", task.exception)
                }
            }

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        ToastHelper.init(this)
        setContent {
            ITForumTheme {
                Root(sharedPreferences)
            }
        }
    }
}




@Composable
fun Root(sharedPreferences:SharedPreferences) {
    var darkTheme by rememberSaveable { mutableStateOf(false) }
    val navHostController = rememberNavController()
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showTopBars = currentRoute in listOf("home","bookmark","follow","tag")
    val showFootBars = currentRoute in listOf("home", "searchscreen", "notification", "personal","bookmark")
    //thay doi ơ day
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val token = sharedPreferences.getString("access_token", null)
    val role = sharedPreferences.getString("role", null)
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })
    userViewModel.getUser()
    val user by userViewModel.user.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }

    if (showSuccessDialog && user?.isBanned == true) {
        SuccessDialog(
            title = "Thông báo!!!",
            color = Color.Red,
            message = "Tài khoản của bạn đã bị khóa đến ngày ${user?.bannedUntil}.",
            nameButton = "Đóng",
            onDismiss = {
                showSuccessDialog = false
                sharedPreferences.edit().remove("role").apply()
                sharedPreferences.edit().remove("access_token").apply()
                sharedPreferences.edit().remove("loginType").apply()
                navHostController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        )
    }
    ITForumTheme(darkTheme = darkTheme)
    {
        if (isTokenExpired(token.toString())) {
            println("Token is expired")
            StartRoot(navHostController, sharedPreferences)
        } else if(user?.isBanned == true){
            showSuccessDialog = true
        } else {
            println("Token is not expired")
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier.width(300.dp)
                    ) {
                        DrawerContent(
                            sidebarItem = sidebarUserItems,
                            navHostController = navHostController,
                            sharedPreferences = sharedPreferences,
                            closedrawer = {
                                if (showTopBars) {
                                    scope.launch {
                                        drawerState.close()
                                    }
                                }
                            }
                        )
                    }
                }
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (showTopBars) {
                            TopBarRoot(
                                navHostController,
                                onMenuClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                },
                            )
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
                        darkTheme = darkTheme,
                        role = role
                    )
                }
            }
        }
    }
}
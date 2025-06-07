package com.example.itforum.user.root

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.itforum.admin.adminReport.ReportAccount.view.ReportedAccountDetailScreen
import com.example.itforum.admin.adminReport.ReportAccount.view.ReportedAccountScreen
import com.example.itforum.admin.adminReport.ReportAccount.viewmodel.ReportViewModelFactory
import com.example.itforum.admin.adminReport.ReportAccount.viewmodel.ReportedAccountDetailViewModel
import com.example.itforum.admin.adminReport.ReportAccount.viewmodel.ReportedAccountDetailViewModelFactory
import com.example.itforum.admin.adminReport.ReportAccount.viewmodel.ReportedUserViewModel
import com.example.itforum.admin.adminReport.ReportPost.view.ReportedPostDetailScreen
import com.example.itforum.admin.adminReport.ReportPost.view.ReportedPostScreen
import com.example.itforum.admin.adminReport.ReportPost.viewmodel.ReportedPostDetailViewModel
import com.example.itforum.admin.adminReport.ReportPost.viewmodel.ReportedPostViewModel
import com.example.itforum.admin.adminReport.ReportPost.viewmodel.ReportedPostViewModelFactory
import com.example.itforum.repository.ReportPostRepository
import com.example.itforum.repository.ReportRepository
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.ReportAccount.view.CreateReportAccountScreen
import com.example.itforum.user.ReportPost.view.CreateReportPostScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //tạo báo cáo tài khoản
//        setContent {
//            ITForumTheme {
//                CreateReportAccountScreen(
//                    reporterUserId = "683c1cd553363395202e2a2a",
//                    reportedUserId = "683deb84c5b3e91c2901b8ba",
//                    onBack = { /*  */ }
//                )
//            }
//        }
        //tạo báo cáo bài viết
//        setContent {
//            ITForumTheme {
//
//                val reporterUserId = "683c1cd553363395202e2a2a"
//                val reportedPostId = "683e2a53a077f04ab64dbe40"
//
//                CreateReportPostScreen(
//                    reporterUserId = reporterUserId,
//                    reportedPostId = reportedPostId,
//                    onBack = {}
//                )
//            }
//        }
        //xem báo cáo tài khoản
//        setContent {
//            ITForumTheme {
//                val navController = rememberNavController()
//
//
//                NavHost(navController = navController, startDestination = "reported_accounts") {
//                    composable("reported_accounts") {
//                        ReportedAccountScreen(navController = navController)
//                    }
//
//                    composable("account_detail/{reportId}") { backStackEntry ->
//                        val reportId = backStackEntry.arguments?.getString("reportId") ?: return@composable
//                        ReportedAccountDetailScreen(reportId = reportId, onBack = { navController.popBackStack() })
//                    }
//                }
        //xem báo cáo bài viết
//                setContent {
//            ITForumTheme {
//                val navController = rememberNavController()
//
//
//                NavHost(navController = navController, startDestination = "reported_posts") {
//                    composable("reported_posts") {
//                        ReportedPostScreen(navController = navController)
//                    }
//
//                    composable("post_detail/{reportId}") { backStackEntry ->
//                        val reportId = backStackEntry.arguments?.getString("reportId") ?: return@composable
//                        ReportedPostDetailScreen(reportId = reportId, onBack = { navController.popBackStack() })
//                    }
//                }


            }
        }






@Composable
fun Root(sharedPreferences:SharedPreferences) {
    var darkTheme by rememberSaveable { mutableStateOf(false) }
    val navHostController = rememberNavController()
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showTopBars = currentRoute in listOf("home")
    val showFootBars = currentRoute in listOf("home", "searchscreen", "notification", "personal")
    //thay doi ơ day
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
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
            println(darkTheme)
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    if (showTopBars) {
                        TopBarRoot(
                            navHostController,
                            onToggleTheme = { darkTheme = !darkTheme },
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
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
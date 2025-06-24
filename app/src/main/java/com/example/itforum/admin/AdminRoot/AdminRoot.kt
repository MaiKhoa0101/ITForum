package com.example.itforum.admin.AdminRoot

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.itforum.admin.adminAccount.AccountDetailScreen
import com.example.itforum.admin.adminAccount.AccountManagementScreen
import com.example.itforum.admin.adminComplaint.ManagementComplaintDetailScreen
import com.example.itforum.admin.adminComplaint.ManagementComplaintScreen
import com.example.itforum.admin.adminController.ControllerManagerScreen
import com.example.itforum.admin.modeldata.sidebarItems
import com.example.itforum.admin.postManagement.PostManagementScreen
import com.example.itforum.user.login.LoginScreen
import com.example.itforum.utilities.DrawerContent
import kotlinx.coroutines.launch
import kotlin.collections.contains
import com.example.itforum.admin.adminCrashlytic.CrashLogScreen
import com.example.itforum.admin.adminNews.CreateNewsScreen
import com.example.itforum.admin.adminNews.ManagementNewsScreen
import com.example.itforum.admin.adminNotification.AdminNotification
import com.example.itforum.admin.adminNotification.CreateNotificationScreen
import com.example.itforum.admin.adminNotification.NotificationDetailScreen
import com.example.itforum.admin.adminPost.PostDetailScreen
import com.example.itforum.admin.adminReport.ReportAccount.view.ReportedAccountDetailScreen
import com.example.itforum.admin.adminReport.ReportAccount.view.ReportedAccountScreen
import com.example.itforum.admin.adminReport.ReportPost.view.ReportedPostDetailScreen
import com.example.itforum.admin.adminReport.ReportPost.view.ReportedPostScreen
import com.example.itforum.admin.components.LocalDrawerOpener
import com.example.itforum.user.news.DetailNewsPage
import com.example.itforum.user.profile.viewmodel.UserViewModel
import com.example.itforum.user.root.Root

@Composable
fun AdminScreen(sharedPreferences: SharedPreferences) {
    val navHostController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showTopBars = currentRoute in listOf(
        "Controller",
        "UserManager",
        "PostManager",
        "ComplaintManager",
        "ReportManager",
        "NewsManager",
        "NotificationManager",
        "Crashlytics",
        "ReportAccount",
        "ReportPost"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
                DrawerContent(
                    sidebarItem = sidebarItems,
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
        CompositionLocalProvider(LocalDrawerOpener provides {
            scope.launch { drawerState.open() }
        }) {
            Scaffold(
                topBar = {
                    if (showTopBars) {
                        HeadBarAdmin(
                            sharedPreferences
//                            opendrawer = {
//                                scope.launch {
//                                    drawerState.open()
//                                }
//                            }
                        )
                    }
                },
            ) { innerPadding ->
                NavHost(navHostController, startDestination = "Controller") {
                    composable("login") {
                        LoginScreen(
                            navHostController = navHostController,
                            sharedPreferences = sharedPreferences,
                            onRegisterClick = { navHostController.navigate("register") },
                            onForgotPasswordClick = { navHostController.navigate("forgot_password") },
                        )
                    }
                    composable("Controller") {
                        ControllerManagerScreen(
                            navHostController,
                            sharedPreferences,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                    composable("UserManager") {
                        AccountManagementScreen(
                            modifier = Modifier.padding(innerPadding),
                            navHostController = navHostController,
                            sharedPreferences
                        )
                    }

                    composable("user_detail/{userId}") {backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")
                        if (userId != null) {
                            AccountDetailScreen(
                                navHostController = navHostController,
                                sharedPreferences = sharedPreferences,
                                userId = userId
                            )
                        }
                    }
                    composable("PostManager") {
                        PostManagementScreen(
                            modifier = Modifier.padding(innerPadding),
                            navHostController = navHostController,
                            sharedPreferences = sharedPreferences
                        )
                    }
                    composable("post_detail/{postId}") {backStackEntry ->
                        val postId = backStackEntry.arguments?.getString("postId")
                        if (postId != null) {
                            PostDetailScreen(
                                navHostController = navHostController,
                                sharedPreferences = sharedPreferences,
                                postId = postId
                            )
                        }
                    }
                    composable("NotificationManager") {
                        AdminNotification(
                            navController = navHostController,
                            sharedPreferences = sharedPreferences,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                    composable("create_notify") {
                        CreateNotificationScreen(
                            navHostController = navHostController,
                            sharedPreferences = sharedPreferences,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                    composable("Crashlytics") {
                        CrashLogScreen(navHostController = navHostController)
                    }
                    composable("ComplaintManager") {
                        ManagementComplaintScreen(
                            navHostController,
                            sharedPreferences,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                    composable("NewsManager") {
                        ManagementNewsScreen(navHostController, sharedPreferences, Modifier.padding(innerPadding))
                    }
                    composable("create_news") {
                        CreateNewsScreen(navHostController, sharedPreferences)
                    }
                    composable("detail_news/{newsId}") { backStackEntry ->
                        val newsId = backStackEntry.arguments?.getString("newsId")
                        if (newsId != null) {
                            DetailNewsPage(newsId, navHostController, sharedPreferences)
                        }
                    }
                    composable("Report_account_detail/{accountId}") {backStackEntry ->
                        val accountId = backStackEntry.arguments?.getString("accountId")
                        if (accountId != null) {
                            ReportedAccountDetailScreen(accountId,
                                onBack = { navHostController.popBackStack() })
                        } else {
                            Text("Không tìm thấy tài khoản.")
                        }

                    }
                    composable("detail_reported_post/{postId}") { backStackEntry ->
                        val postId = backStackEntry.arguments?.getString("postId")
                        if (postId != null) {
                            ReportedPostDetailScreen(postId,
                                onBack = { navHostController.popBackStack() })
                        } else {
                            androidx.compose.material.Text("Không tìm thấy tài khoản.")
                        }
                        }
                    composable("complaint_detail/{complaintId}") { backStackEntry ->
                        val complaintId = backStackEntry.arguments?.getString("complaintId")
                        if (complaintId != null) {
                            ManagementComplaintDetailScreen(
                                navHostController,
                                sharedPreferences,
                                complaintId
                            )
                        } else {
                            androidx.compose.material.Text("Không tìm thấy khiếu nại.")
                        }
                    }
                    composable("ReportPost") {
                        ReportedPostScreen(
                            navController = navHostController,
                            sharedPreferences,
                            Modifier.padding(innerPadding)
                        )
                    }
                    composable("ReportAccount") {
                        ReportedAccountScreen(
                           navController = navHostController,
                            sharedPreferences,
                            Modifier.padding(innerPadding)
                        )
                    }
                    composable("root"){
                        Root(sharedPreferences)
                    }
                    composable("detail_notify") {
                        NotificationDetailScreen(
                            navHostController = navHostController,
                            sharedPreferences = sharedPreferences
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun HeadBarAdmin(
    sharedPreferences: SharedPreferences
){
    var userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })
    LaunchedEffect(Unit) {
        userViewModel.getUser()
    }
    val user by userViewModel.user.collectAsState()
    val openDrawer = LocalDrawerOpener.current
    Box(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .background(color = Color.Cyan).padding(vertical = 5.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            imageVector = Icons.Default.Monitor,
            contentDescription = "Logo",
            modifier = Modifier
                .size(50.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Icon (Menu)
            IconButton(
                onClick = { openDrawer() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu Icon",
                    tint = Color.Unspecified // Keeps the original icon color
                )
            }
            // Right User Info and Logout
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // User Greeting
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Xin chào, "+ (user?.name ?: "Người dùng"),
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }

//                // Logout Button
//                IconButton(
//                    onClick = { viewModel.logout(context) },
//                    modifier = Modifier.size(40.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.logout),
//                        contentDescription = "Logout",
//                        tint = Color.Black,
//                        modifier = Modifier.size(30.dp)
//                    )
//                }
            }
        }
    }
}

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
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.itforum.admin.adminAccount.AccountManagementScreen
import com.example.itforum.admin.adminController.ControllerManagerScreen
import com.example.itforum.admin.modeldata.sidebarItems
import com.example.itforum.admin.postManagement.PostManagementScreen
import com.example.itforum.user.login.LoginScreen
import com.example.itforum.utilities.DrawerContent
import kotlinx.coroutines.launch
import kotlin.collections.contains
import com.example.itforum.admin.adminCrashlytic.CrashLogScreen
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
        "ReportManager",
        "NewsManager",
        "NotificationManager",
        "Crashlytics"
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
            topBar = {
                if (showTopBars) {
                    HeadBarAdmin(
                        opendrawer = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    )
                }
            },
        ){ innerPadding ->
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
                        modifier = Modifier.padding(innerPadding))
                }
                composable("UserManager") {
                    AccountManagementScreen(
                        modifier = Modifier.padding(innerPadding),
                        navHostController = navHostController,
                        users = emptyList(),
                        sharedPreferences
                    )
                }
                composable("ReportManager") {
                    PostManagementScreen(
                        modifier = Modifier.padding(innerPadding),
                        navHostController = navHostController,
                        posts = emptyList()
                    )
                }
                composable("PostManager") {
                    PostManagementScreen(
                        modifier = Modifier.padding(innerPadding),
                        navHostController = navHostController,
                        posts = emptyList()
                    )
                }
                composable("NewsManager") {

                }
                composable("NotificationManager") {

                }
                composable("Crashlytics") {
                    CrashLogScreen(navHostController = navHostController)
                }

            }
        }
    }
}

@Composable
fun HeadBarAdmin(opendrawer:()->Unit){
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
                onClick = { opendrawer() },
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
                        text = "Xin chào,",
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

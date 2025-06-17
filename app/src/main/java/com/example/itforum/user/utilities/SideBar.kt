package com.example.itforum.utilities

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.itforum.admin.modeldata.SidebarItem


@Composable
fun DrawerContent(
    sidebarItem: List<SidebarItem>, // List of sidebar items
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    closedrawer: () -> Unit,
    modifier: Modifier = Modifier
    ) {
    // Track the currently selected item
    val currentDestination = navHostController.currentDestination?.route

    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { closedrawer() },
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu Icon",
                    tint = Color.Cyan // Keeps the original icon color
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            // Optional Header
            Text(
                text = "Menu",
                fontSize = 20.sp
            )
        }

        // Use LazyColumn to lazily load the menu items
        LazyColumn(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            items(sidebarItem) { item ->
                DrawerMenuItem(
                    iconResId = item.iconField, // Use the icon field from SidebarItem...,
                    label = item.nameField,
                    isSelected = currentDestination == item.navigationField,
                    onClick = {
                        if(item.navigationField == "root"){
                            val removeRole = sharedPreferences.edit().remove("role").apply()
                            val removeToken = sharedPreferences.edit().remove("access_token").apply()
                        }
                        navHostController.navigate(item.navigationField) {
                            // Avoid multiple copies of the same destination in the back stack
                            popUpTo(navHostController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                            closedrawer()
                        }
                    }
                )
            }
        }
    }
//    Surface(
//        tonalElevation = 8.dp,
//        modifier = Modifier
//            .fillMaxHeight()
//            .width(260.dp) // Chiếm góc trái
//            .background(Color(0xFFB0BEC5))
//    ) {
//
//        Column(modifier = Modifier.fillMaxSize()) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 1.dp, end = 12.dp, top = 22.dp, bottom = 8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(onClick = { onCloseDrawer() }) {
//                    Icon(
//                        imageVector = Icons.Default.Menu,
//                        contentDescription = "Close Drawer"
//                    )
//                }
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    text = "Tiện ích", fontSize = 23.sp,
//                    style = MaterialTheme.typography.titleMedium
//                )
//            }
//            Spacer(modifier = Modifier.height(8.dp))
//            Divider()
//            Text(
//                text = "Ghi chú",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable {
//                        onSelectNote()
//                        onCloseDrawer()
//                    }
//                    .padding(16.dp)
//            )
//            Divider()
//            Text(
//                text = "ChatAI",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable {
//                        onSelectChatAI()
//                        onCloseDrawer()
//                    }
//                    .padding(16.dp)
//            )
//        }
//    }
}

@Composable
fun DrawerMenuItem(
    iconResId: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = iconResId,
                contentDescription = label,
                modifier = Modifier.size(30.dp)
            )
        },
        label = {
            Text(text = label)
        },
        selected = isSelected,
        onClick = onClick,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

package com.example.itforum.admin.modeldata

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.ui.graphics.vector.ImageVector

data class SidebarItem(
    val nameField: String,
    val iconField: ImageVector,
    val navigationField: String = ""
)

// Sidebar item list
val sidebarItems = listOf(
    SidebarItem(
        nameField = "Bảng điều khiển",
        iconField = Icons.Default.Monitor,
        navigationField = "Controller"
    ),
    SidebarItem(
        nameField = "Quản lý tài khoản",
        iconField = Icons.Default.Person,
        navigationField = "UserManager"
    ),
    SidebarItem(
        nameField = "Quản lý bài viết",
        iconField = Icons.Default.Interests,
        navigationField = "PostManager"
    ),
    SidebarItem(
        nameField = "Quản lý khiếu nại",
        iconField = Icons.Default.Report,
        navigationField = "ReportManager"
    ),
    SidebarItem(
        nameField = "Quản lý tin tức",
        iconField = Icons.Default.Newspaper,
        navigationField = "NewsManager"
    ),

    SidebarItem(
        nameField = "Quản lí thông báo trong hệ thống",
        iconField = Icons.Default.Message,
        navigationField = "NotificationManager"
    ),

    )

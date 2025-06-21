package com.example.itforum.admin.modeldata

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.ReportGmailerrorred
import androidx.compose.material.icons.filled.ReportProblem
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
        navigationField = "ComplaintManager"
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
    SidebarItem(
        nameField = "Quản lý crash hệ thống",
        iconField = Icons.Default.Message,
        navigationField = "Crashlytics"
),
    SidebarItem(
        nameField = "Quản lý báo cáo tài khoản",
        iconField = Icons.Default.ReportProblem,
        navigationField = "ReportAccount"
    ),
    SidebarItem(
        nameField = "Quản lý báo cáo bài viết",
        iconField = Icons.Default.ReportGmailerrorred,
        navigationField = "ReportPost"

    ),
    SidebarItem(
        nameField = "Đăng xuất",
        iconField = Icons.Default.Logout,
        navigationField = "root"
    )
)

// Sidebar item list
val sidebarUserItems = listOf(
    SidebarItem(
        nameField = "Chat AI",
        iconField = Icons.Default.Lightbulb,
        navigationField = "chat"
    ),
    SidebarItem(
        nameField = "Ghi chú",
        iconField = Icons.Default.NoteAlt,
        navigationField = "note"
    )
)

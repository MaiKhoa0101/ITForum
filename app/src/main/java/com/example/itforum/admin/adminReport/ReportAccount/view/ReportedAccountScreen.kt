package com.example.itforum.admin.adminReport.ReportAccount.view

import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.itforum.admin.components.TableData
import com.example.itforum.admin.adminReport.ReportAccount.viewmodel.ReportedUserViewModel
import com.example.itforum.admin.adminReport.ReportAccount.model.response.ReportedUser
import com.example.itforum.admin.components.AdminScreenLayout
import com.example.itforum.user.post.icontext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility

@Composable
fun ReportedAccountScreen(
    viewModel: ReportedUserViewModel,
    navController: NavHostController,
    sharedPreferences: SharedPreferences
) {
    val users by viewModel.users.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    val menuOptions = listOf(
        icontext(Icons.Default.Visibility, "Xem chi tiết"),
        icontext(Icons.Default.Delete, "Xóa")
    )

    AdminScreenLayout(
        title = "Quản lý báo cáo người dùng",
        itemCount = users.size
    ) { searchText, _, _ ->
        val filteredUsers = users.filter {
            it.username.contains(searchText, ignoreCase = true) ||
                    it.email.contains(searchText, ignoreCase = true)
        }

        if (error != null) {
            Text("Lỗi: $error", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        TableData(
            headers = listOf("ID", "Tên", "Email", "Số báo cáo"),
            rows = convertReportedUsersToRows(filteredUsers),
            menuOptions = menuOptions,
            sharedPreferences = sharedPreferences,
            onClickOption = { accountId ->
                navController.navigate("account_detail/$accountId")
            }
        )
    }
}

fun convertReportedUsersToRows(users: List<ReportedUser>): List<List<String>> {
    return users.map {
        listOf(it._id, it.username, it.email, it.reportCount.toString())
    }
}

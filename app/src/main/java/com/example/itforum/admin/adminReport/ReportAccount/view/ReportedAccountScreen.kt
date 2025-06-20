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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.itforum.admin.adminReport.ReportAccount.viewmodel.ReportViewModelFactory
import com.example.itforum.admin.components.convertToTableRows
import com.example.itforum.repository.ReportRepository
import com.example.itforum.retrofit.RetrofitInstance

@Composable
fun ReportedAccountScreen(navController: NavHostController,
                          sharedPreferences: SharedPreferences) {
    val viewModel: ReportedUserViewModel = viewModel(factory = ReportViewModelFactory(
        ReportRepository(
            RetrofitInstance.reportAccountService)
    ))
    val users by viewModel.users.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    val menuOptions = listOf(
        icontext(Icons.Default.Edit,"Xem chi tiết",{accountId ->
            println("DEBUG - ID được chọn: $accountId")
            navController.navigate("Report_account_detail/$accountId")
        })
    )

    AdminScreenLayout(
        title = "Quản lý báo cáo người dùng",
        itemCount = users.size,
        searchTable = { searchText->
            val filteredUsers = users.filter {
                it._id .contains(searchText, ignoreCase = true) ||
                        it.reason.contains(searchText, ignoreCase = true)||
                        it.reportedUserId.contains(searchText, ignoreCase = true)||
                        it.createdAt.contains(searchText, ignoreCase = true)
            }

            if (error != null) {
                Text("Lỗi: $error", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            TableData(
                headers = listOf("ID báo cáo", "ID người bị báo cáo", "ID người báo cáo", "Lý do", "Thời gian","Tùy chỉnh"),
                rows = convertToTableRows(filteredUsers),
                menuOptions = menuOptions,
                sharedPreferences = sharedPreferences

            )
        }
    )
}
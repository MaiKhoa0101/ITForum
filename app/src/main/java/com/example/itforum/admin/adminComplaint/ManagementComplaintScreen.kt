package com.example.itforum.admin.adminComplaint

import android.content.SharedPreferences
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.itforum.admin.components.TableData
import com.example.itforum.admin.components.convertToTableRows
import com.example.itforum.admin.adminComplaint.viewmodel.ComplaintViewModel
import com.example.itforum.admin.components.AdminScreenLayout
import com.example.itforum.user.post.icontext

@Composable
fun ManagementComplaintScreen (
    navController: NavHostController,
    sharedPreferences: SharedPreferences
) {
    var complaintViewModel: ComplaintViewModel = viewModel()
    LaunchedEffect(Unit) {
        complaintViewModel.getComplaint()
    }
    val complaints by complaintViewModel.listComplaint.collectAsState()
    val menuOptions = listOf(
        icontext(Icons.Default.Edit,"Xem chi tiết"),
        icontext(Icons.Default.Build,"Xử lý")
    )
    complaints?.let {
        AdminScreenLayout(
        title = "Quản lý báo cáo người dùng",
        itemCount = it.size
    ) { searchText, _, _ ->
        val filteredUsers = complaints!!.filter {
            it.userId.contains(searchText, ignoreCase = true) ||
                    it.status.contains(searchText, ignoreCase = true)
        }
        TableData(
            headers = listOf("ID", "Người dùng", "Tiêu đề", "Lý do", "Thời gian", "Trạng thái", "Tùy chỉnh"),
            rows = convertToTableRows(filteredUsers),
            menuOptions = menuOptions,
            sharedPreferences = sharedPreferences,
            onClickOption = { complaintId ->
                navController.navigate("complaint_detail/$complaintId")
            }
        )
    }
    }
}
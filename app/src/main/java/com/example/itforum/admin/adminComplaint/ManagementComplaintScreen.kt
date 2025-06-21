package com.example.itforum.admin.adminComplaint

import android.content.SharedPreferences
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.itforum.admin.components.TableData
import com.example.itforum.admin.components.convertToTableRows
import com.example.itforum.admin.adminComplaint.viewmodel.ComplaintViewModel
import com.example.itforum.admin.components.AdminScreenLayout
import com.example.itforum.admin.components.TableRowConvertible
import com.example.itforum.user.complaint.SuccessDialog
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.modelData.response.Complaint
import com.example.itforum.user.modelData.response.News
import com.example.itforum.user.post.icontext
import java.time.Instant

@Composable
fun ManagementComplaintScreen (
    navController: NavHostController,
    sharedPreferences: SharedPreferences,
    modifier: Modifier
) {
    var complaintViewModel: ComplaintViewModel = viewModel()
    LaunchedEffect(Unit) {
        complaintViewModel.getAllComplaint()
    }
    val uiState by complaintViewModel.uiStateCreate.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        println("UI State duoc thay doi")
        if (uiState is UiState.Success) {
            println("uiState là success")
            showSuccessDialog = true
        }
    }
    // UI hiển thị
    if (showSuccessDialog) {
        SuccessDialog(
            message = "Xử lý thành công!",
            onDismiss = {
                showSuccessDialog = false
                navController.navigate("ComplaintManager")
            }
        )
    }
    val complaints by complaintViewModel.listComplaint.collectAsState()
    val menuOptions = listOf(
        icontext(Icons.Default.Edit,"Xem chi tiết",{ complaintId ->
            navController.navigate("complaint_detail/$complaintId")
        }),
        icontext(Icons.Default.Build,"Chấp nhận",{complaintId ->
            complaintViewModel.handleApproved(complaintId)
        }),
        icontext(Icons.Default.Close,"Từ chối",{complaintId ->
            complaintViewModel.handleRejected(complaintId)
        })
    )
    val filterOptions = mapOf(
        "Thời gian" to listOf("Mới nhất", "Cũ nhất"),
        "Trạng thái" to listOf("Chưa xử lý", "Đã xử lý", "Từ chối")
    )
    complaints?.let {
        AdminScreenLayout(
            title = "Quản lý khiếu nại người dùng",
            itemCount = it.size,
            modifier = modifier,
            searchTable = { searchText ->
                val dataFiltered = complaints!!.filter { item ->
                    searchText.isBlank() || listOf(
                        item.userId,
                        item.createdAt,
                        item.title,
                        item.reason
                    ).any { it.contains(searchText, ignoreCase = true) }
                }
                return@AdminScreenLayout dataFiltered
            },
            filterOptions = filterOptions,
            filterField = { field, value, data ->
                data as List<Complaint>
                val dataFiltered = when (field) {
                    "Thời gian" -> {
                        when (value) {
                            "Mới nhất" -> data.sortedByDescending { Instant.parse(it.createdAt) }
                            "Cũ nhất" -> data.sortedBy { Instant.parse(it.createdAt) }
                            else -> data
                        }
                    }
                    "Trạng thái"->{
                        when (value) {
                            "Chưa xử lý" -> data.filter { it.status == "pending" }
                            "Đã xử lý" -> data.filter { it.status == "approved" }
                            "Từ chối" -> data.filter { it.status == "rejected" }
                            else -> data
                        }
                    }
                    else -> data
                }
                return@AdminScreenLayout dataFiltered
            },
            table = {dataFiltered ->
                TableData(
                    headers = listOf("ID", "Người dùng", "Tiêu đề", "Lý do", "Thời gian", "Trạng thái", "Tùy chỉnh"),
                    rows = convertToTableRows(dataFiltered as List<TableRowConvertible>),
                    menuOptions = menuOptions,
                    sharedPreferences = sharedPreferences
                )
            }
        )
    }
}
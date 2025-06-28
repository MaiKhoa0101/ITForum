package com.example.itforum.admin.adminNotification

import android.content.SharedPreferences
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.itforum.admin.components.AdminScreenLayout
import com.example.itforum.admin.components.TableData
import com.example.itforum.admin.components.TableRowConvertible
import com.example.itforum.admin.components.convertToTableRows
import com.example.itforum.user.modelData.response.Notification
import com.example.itforum.user.notification.viewmodel.NotificationViewModel
import com.example.itforum.user.post.icontext
import java.time.Instant

@Composable
fun AdminNotification (
    navController: NavHostController,
    sharedPreferences: SharedPreferences,
    modifier: Modifier
) {
    val context = LocalContext.current
    val notificationViewModel: NotificationViewModel = viewModel(factory = viewModelFactory {
        initializer { NotificationViewModel(sharedPreferences) }
    })
    val notificationList by notificationViewModel.notificationList.collectAsState()
    LaunchedEffect(Unit) {
        notificationViewModel.getAllNotifications()
    }
    val menuOptions = listOf(
        icontext(Icons.Default.Edit,"Xem chi tiết",{ notificationId ->
            navController.navigate("detail_notify/$notificationId")
        }),
        icontext(Icons.Default.Delete,"Xóa",{ notificationId -> })
    )
    val filterOptions = mapOf(
        "Thời gian" to listOf("Mới nhất", "Cũ nhất"),
        "Trạng thái" to listOf("Đã gửi", "Nháp")
    )

    notificationList.let {
        AdminScreenLayout(
            title = "Quản lý thông báo",
            itemCount = it.size,
            modifier = modifier,
            addComposed = {
                Button(
                    onClick = {
                        navController.navigate("create_notify")
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF2196F3),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Thêm mới",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(3.dp),
                    )
                }
            },
            searchTable = { searchText ->
                val dataFiltered = notificationList.filter { item ->
                    searchText.isBlank() || listOf(
                        item.id,
                        item.createdAt,
                        item.title,
                        item.content,
                        item.userReceiveNotificationId?:"Tất cả"
                    ).any { it.contains(searchText, ignoreCase = true) }
                }
                return@AdminScreenLayout dataFiltered
            },
            filterOptions = filterOptions,
            filterField = { field, value, data ->
                data as List<Notification>
                val dataFiltered = when (field) {
                    "Thời gian" -> {
                        when (value) {
                            "Mới nhất" -> data.sortedByDescending { Instant.parse(it.createdAt) }
                            "Cũ nhất" -> data.sortedBy { Instant.parse(it.createdAt) }
                            else -> data
                        }
                    }
                    else -> data
                }
                return@AdminScreenLayout dataFiltered
            },
            table = {dataFiltered ->
                TableData(
                    headers = listOf("ID", "Tiêu đề", "Nội dung", "Người nhận", "Bài viết", "Thời gian", "Tùy chỉnh"),
                    rows = convertToTableRows(dataFiltered as List<TableRowConvertible>),
                    menuOptions = menuOptions,
                    sharedPreferences = sharedPreferences,
                )
            }
        )
    }
}
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.itforum.admin.components.AdminScreenLayout
import com.example.itforum.admin.components.TableData
import com.example.itforum.admin.components.TableRowConvertible
import com.example.itforum.admin.components.convertToTableRows
import com.example.itforum.user.post.icontext
import java.time.Instant

data class Notification(
    val id: String,
    val title: String,
    val content: String,
    val receiver: String,
    val createdAt: String,
    val status: String,
    ) : TableRowConvertible {
    override fun toTableRow(): List<String> {
        return listOf(id, title, content, receiver, createdAt, status)
    }
}

@Composable
fun AdminNotification (
    navController: NavHostController,
    sharedPreferences: SharedPreferences,
    modifier: Modifier
) {
    val context = LocalContext.current

    val menuOptions = listOf(
        icontext(Icons.Default.Edit,"Xem chi tiết",{ notifyId ->
            navController.navigate("detail_notify")
        }),
        icontext(Icons.Default.Delete,"Xóa",{ notifyId -> })
    )
    val filterOptions = mapOf(
        "Thời gian" to listOf("Mới nhất", "Cũ nhất"),
        "Trạng thái" to listOf("Đã gửi", "Nháp")
    )
    var listNotify = listOf(
        Notification("1","Bug alert","hahahahaha","Tất cả","12/04/25","Đã gửi"),
        Notification("2","New update","hahahahaha","Tất cả","11/04/25","Đã gửi"),
        Notification("3","Test msg","hahahahaha","Tất cả","10/04/25","Đã gửi"),
        Notification("4","eror 404","hahahahaha","Tất cả","10/03/25","Đã gửi"),
        Notification("5","Bảo trì","hahahahaha","Tất cả","08/03/25","Đã gửi"),
        Notification("6","eror 404","hahahahaha","Tất cả","12/04/25","Đã gửi"),
        Notification("7","Bug alert","hahahahaha","Tất cả","10/03/25","Đã gửi"),
        )
    listNotify.let {
        AdminScreenLayout(
            title = "Quản lý tin tức",
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
                val dataFiltered = listNotify.filter { item ->
                    searchText.isBlank() || listOf(
                        item.id,
                        item.createdAt,
                        item.title,
                        item.content,
                        item.receiver
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
                    headers = listOf("ID", "Tiêu đề", "Nội dung", "Người nhận", "Thời gian", "Trạng thái","Tùy chỉnh"),
                    rows = convertToTableRows(dataFiltered as List<TableRowConvertible>),
                    menuOptions = menuOptions,
                    sharedPreferences = sharedPreferences,
                )
            }
        )
    }
}
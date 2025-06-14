package com.example.itforum.admin.adminAccount


import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.itforum.admin.adminAccount.model.UserAccountManagerAdmin
import com.example.itforum.admin.components.TableData
import com.example.itforum.admin.components.convertToTableRows
import com.example.itforum.user.post.icontext
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

import java.time.LocalDate

@Composable
fun AccountManagementScreen(
    modifier: Modifier,
    navHostController: NavHostController,
    users: List<UserAccountManagerAdmin>,
    sharedPreferences: SharedPreferences
) {
    var searchText by remember { mutableStateOf("") }
    val dateDialogStateStart = rememberMaterialDialogState()
    val dateDialogStateEnd = rememberMaterialDialogState()
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDateFilters by remember { mutableStateOf(false) }

    var filteredUsers by remember { mutableStateOf(users) }
    val headers = listOf("ID", "Ten", "email", "sdt", "ngay tao", "chi tiet")
    val menuOptions = listOf(
        icontext(Icons.Default.Edit,"Xem chi tiết"),
        icontext(Icons.Default.Delete,"Xóa")
    )
    Column(modifier = modifier.fillMaxWidth().background(Color.White)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(69.dp)
                .background(Color(0xFF00AEFF))
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(start = 25.dp, end = 25.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "xin chao user", color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.AccountCircle, contentDescription = "User")
                }
            }
        }
        Spacer(modifier = Modifier.height(17.dp))
        Column(modifier = Modifier.padding(horizontal = 25.dp)) {
            Text(text = "Quan li tai khoan", fontSize = 20.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Tìm kiếm") }
                )
                IconButton(onClick = {
                    filteredUsers = users.filter {
                        it.userName.contains(searchText, ignoreCase = true) ||
                                it.email.contains(searchText, ignoreCase = true) ||
                                it.sdt.contains(searchText)
                    }
                }) {
                    Icon(Icons.Default.Search, contentDescription = "search", tint = Color.Black)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    showDateFilters = !showDateFilters
                }) {
                    Text("Lọc")
                }
            }

            if (showDateFilters) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE3F2FD)) // nền xanh nhạt
                        .padding(12.dp)
                        .border(1.dp, Color(0xFF90CAF9), shape = RoundedCornerShape(8.dp))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(
                            " ngày bắt đầu:",
                            color = Color(0xFF0D47A1),
                            modifier = Modifier.width(130.dp)
                        )
                        Button(
                            onClick = { dateDialogStateStart.show() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90CAF9))
                        ) {
                            Text(startDate?.toString() ?: "Chọn", color = Color.White)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(
                            "ngày kết thúc:",
                            color = Color(0xFF0D47A1),
                            modifier = Modifier.width(130.dp)
                        )
                        Button(
                            onClick = { dateDialogStateEnd.show() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90CAF9))
                        ) {
                            Text(endDate?.toString() ?: "Chọn", color = Color.White)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                filteredUsers = users.filter {
                                    val matchesSearch = searchText.isBlank() ||
                                            it.userName.contains(searchText, ignoreCase = true) ||
                                            it.email.contains(searchText, ignoreCase = true) ||
                                            it.sdt.contains(searchText)
                                    val matchesDate = (startDate == null || !it.createdDate.isBefore(startDate)) &&
                                            (endDate == null || !it.createdDate.isAfter(endDate))
                                    matchesSearch && matchesDate
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                        ) {
                            Text("Áp dụng", color = Color.White)
                        }
                        Button(
                            onClick = {
                                startDate = null
                                endDate = null
                                filteredUsers = users
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                        ) {
                            Text("Xóa lọc", color = Color.White)
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier.size(width = 200.dp, height = 50.dp).background(Color(0xFF7BD88F)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "tong so user: ${filteredUsers.size}")
            }
            Spacer(modifier = Modifier.height(16.dp))

            TableData(
                headers,
                rows = convertToTableRows(filteredUsers),
                menuOptions,
                sharedPreferences = sharedPreferences,
                onClickOption = { accountId ->
                    navHostController.navigate("account_detail/$accountId")
                }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(60.dp).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text("© 2025 IT Forum Admin", color = Color.Gray, fontSize = 14.sp)
        }
    }

    MaterialDialog(
        dialogState = dateDialogStateStart,
        buttons = {
            positiveButton("OK")
            negativeButton("Hủy")
        }
    ) {
        datepicker(
            initialDate = startDate ?: LocalDate.now(),
            title = "ngày bắt đầu"
        ) {
            startDate = it
        }
    }

    MaterialDialog(
        dialogState = dateDialogStateEnd,
        buttons = {
            positiveButton("OK")
            negativeButton("Hủy")
        }
    ) {
        datepicker(
            initialDate = endDate ?: LocalDate.now(),
            title = "ngày kết thúc"
        ) {
            endDate = it
        }
    }
}



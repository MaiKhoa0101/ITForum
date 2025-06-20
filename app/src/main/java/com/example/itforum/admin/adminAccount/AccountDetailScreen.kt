package com.example.itforum.admin.adminAccount

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.itforum.admin.adminPost.DropdownMenuBox
import com.example.itforum.admin.components.DetailItem
import com.example.itforum.admin.components.DetailScreenLayout
import java.time.LocalDate

//
//@Composable
//fun AccountDetailScreen(accountId: String?) {
//    val account = remember(accountId) { generateAccounts().find { it.id.toString() == accountId } }
//    var isLocked by remember { mutableStateOf(false) }
//    var selectedDuration by remember { mutableStateOf("1 tháng") }
//
//    DetailScreenLayout(title = "Chi tiết tài khoản") {
//        if (account != null) {
//            Text("Thông tin:", style = MaterialTheme.typography.titleLarge)
//
//            DetailItem("ID", account.id.toString())
//            DetailItem("Tên", account.userName)
//            DetailItem("Email", account.email)
//            DetailItem("SĐT", account.sdt)
//            DetailItem("Ngày tạo", account.createdDate.toString())
//            DetailItem("Trạng thái", if (isLocked) "Đã khóa" else "Đang hoạt động")
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            DropdownMenuBox(
//                selectedOption = selectedDuration,
//                options = listOf("1 tuần", "1 tháng", "1 năm", "Vĩnh viễn")
//            ) { selectedDuration = it }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
//                Button(onClick = { isLocked = !isLocked }) {
//                    Text(if (isLocked) "Mở khóa" else "Khóa")
//                }
//                OutlinedButton(onClick = {
//                    println("Cảnh báo gửi đến ${account.email}")
//                }) {
//                    Text("Cảnh báo")
//                }
//            }
//        } else {
//            Text("Không tìm thấy tài khoản.", color = Color.Red)
//        }
//    }
//}


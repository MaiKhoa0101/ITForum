package com.example.itforum.admin.adminAccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itforum.admin.adminAccount.model.UserAccountManagerAdmin
import com.example.itforum.admin.adminPost.DropdownMenuBox
import com.example.itforum.admin.components.DetailItem
import com.example.itforum.admin.components.DetailScreenLayout
import java.time.LocalDate

fun generateAccounts(): List<UserAccountManagerAdmin> { // Definition 1
    return List(20) {
        UserAccountManagerAdmin(
            id = it + 1,
            userName = "Người dùng $it",
            email = "user$it@example.com",
            sdt = "01234567${it}9",
            createdDate = LocalDate.of(2024, 1, 1).plusDays(it.toLong())
        ) 
    }
}@Composable
fun AccountDetailScreen(accountId: Int) {
    val account = remember(accountId) { generateAccounts().find { it.id == accountId } }
    var isLocked by remember { mutableStateOf(false) }
    var selectedDuration by remember { mutableStateOf("1 tháng") }

    DetailScreenLayout(title = "Chi tiết tài khoản") {
        if (account != null) {
            Text("Thông tin:", style = MaterialTheme.typography.titleLarge)

            DetailItem("ID", account.id.toString())
            DetailItem("Tên", account.userName)
            DetailItem("Email", account.email)
            DetailItem("SĐT", account.sdt)
            DetailItem("Ngày tạo", account.createdDate.toString())
            DetailItem("Trạng thái", if (isLocked) "Đã khóa" else "Đang hoạt động")

            Spacer(modifier = Modifier.height(16.dp))

            DropdownMenuBox(
                selectedOption = selectedDuration,
                options = listOf("1 tuần", "1 tháng", "1 năm", "Vĩnh viễn")
            ) { selectedDuration = it }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { isLocked = !isLocked }) {
                    Text(if (isLocked) "Mở khóa" else "Khóa")
                }
                OutlinedButton(onClick = {
                    println("Cảnh báo gửi đến ${account.email}")
                }) {
                    Text("Cảnh báo")
                }
            }
        } else {
            Text("Không tìm thấy tài khoản.", color = Color.Red)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAccountDetailScreen() {
    AccountDetailScreen(accountId = 1)
}

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
}
@Composable
fun AccountDetailScreen(accountId: Int) {
    val fakeAccount = remember(accountId) {
        generateAccounts().find { it.id == accountId }
    }

    val isAccountLocked = remember { mutableStateOf(false) }
    val lockDurationOptions = listOf("1 tuần", "1 tháng", "1 năm", "Vĩnh viễn")
    var selectedDuration by remember { mutableStateOf(lockDurationOptions[0]) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(69.dp)
                .background(Color(0xFF00AEFF))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 25.dp, end = 25.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Xin chào admin", color = Color.White, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "User",
                        tint = Color.White
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (fakeAccount != null) {
                Text(
                    "Chi tiết tài khoản:",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                DetailItem("ID:", fakeAccount.id.toString())
                Divider(color = Color.LightGray, thickness = 1.dp)
                DetailItem("Tên:", fakeAccount.userName)
                Divider(color = Color.LightGray, thickness = 1.dp)
                DetailItem("Email:", fakeAccount.email)
                Divider(color = Color.LightGray, thickness = 1.dp)
                DetailItem("SĐT:", fakeAccount.sdt)
                Divider(color = Color.LightGray, thickness = 1.dp)
                DetailItem("Ngày tạo:", fakeAccount.createdDate.toString())
                Divider(color = Color.LightGray, thickness = 1.dp)
                DetailItem("Trạng thái:", if (isAccountLocked.value) "Đã khóa" else "Đang hoạt động")
                Divider(color = Color.LightGray, thickness = 1.dp)

                if (!isAccountLocked.value) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Chọn thời gian khóa tài khoản:", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    DropdownMenuBox(
                        selectedOption = selectedDuration,
                        options = lockDurationOptions
                    ) { selected ->
                        selectedDuration = selected
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            isAccountLocked.value = !isAccountLocked.value
                            if (isAccountLocked.value) {
                                println("Tài khoản bị khóa trong $selectedDuration")
                            } else {
                                println("Tài khoản đã được mở khóa")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAccountLocked.value) Color(0xFF4CAF50) else Color(0xFFFF5722)
                        )
                    ) {
                        Text(if (isAccountLocked.value) "Mở khóa tài khoản" else "Khóa tài khoản")
                    }

                    OutlinedButton(onClick = {
                        println("Gửi cảnh cáo đến ${fakeAccount.email}")
                    }) {
                        Text("Gửi cảnh cáo")
                    }
                }
            } else {
                Text("Không tìm thấy tài khoản.", color = Color.Red)
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = Color(0xFF555555),
            fontSize = 16.sp,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            color = Color.Black,
            fontSize = 16.sp
        )
    }
}

@Composable
fun DropdownMenuBox(
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentSize(Alignment.TopStart)) {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedOption)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { label ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onOptionSelected(label)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAccountDetailScreen() {
    AccountDetailScreen(accountId = 1)
}

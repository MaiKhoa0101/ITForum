package com.example.itforum.admin.adminAccount

import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.itforum.admin.adminPost.DropdownMenuBox
import com.example.itforum.admin.components.DetailItem
import com.example.itforum.admin.components.DetailScreenLayout
import com.example.itforum.user.modelData.response.UserProfileResponse
import com.example.itforum.user.profile.viewmodel.UserViewModel
import java.time.LocalDate


@Composable
fun AccountDetailScreen(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    userId: String
) {
    var userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })
    var user by remember { mutableStateOf<UserProfileResponse?>(null) }
    LaunchedEffect(Unit) {
        user = userViewModel.getUser(userId)
    }
    var isLocked by remember { mutableStateOf(false) }
    var selectedDuration by remember { mutableStateOf("1 tháng") }

    DetailScreenLayout(
        title = "Chi tiết tài khoản",
        onBack = {navHostController.popBackStack()}
    ) {
        Text("Thông tin:", style = MaterialTheme.typography.titleLarge)
        if (user != null) {
            DetailItem("ID", user!!.id)
            DetailItem("Tên", user!!.name)
            DetailItem("Email", user!!.email)
            DetailItem("SĐT", user!!.phone)
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
                    println("Cảnh báo gửi đến ${user!!.email}")
                }) {
                    Text("Cảnh báo")
                }
            }
        }
    }
}


package com.example.itforum.user.ReportAccount.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.itforum.repository.ReportRepository
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.ui.theme.MainTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportAccountScreen(
    reporterUserId: String,
    reportedUserId: String,
    onBack: () -> Unit
) {
    val repository = ReportRepository(RetrofitInstance.reportAccountService)
    val scope = rememberCoroutineScope()
    var reason by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val suggestionReasons = listOf(
        "Spam hoặc quảng cáo",
        "Tài khoản giả mạo",
        "Quấy rối hoặc bắt nạt",
        "Đăng nội dung nhạy cảm",
        "Tuyên truyền thù ghét",
        "Lừa đảo hoặc gian lận",
        "Tài khoản vi phạm điều khoản sử dụng",
        "Tài khoản đăng bài không liên quan",
        "Lợi dụng nền tảng để bán hàng trái phép"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Tố cáo tài khoản", color = MaterialTheme.colorScheme.onPrimaryContainer)
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MainTheme)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Lý do tố cáo:",
                style = MaterialTheme.typography.titleLarge
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Chọn lý do có sẵn:")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    suggestionReasons.forEach { item ->
                        AssistChip(
                            onClick = { reason = item },
                            label = { Text(item) }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Nhập lý do") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                )
            )

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        successMessage = null
                        errorMessage = null
                        try {
                            val result = repository.createReportAccount(
                                reportedUserId = reportedUserId,
                                reporterUserId = reporterUserId,
                                reason = reason
                            )
                            if (result.isSuccessful) {
                                successMessage = "Đã gửi tố cáo thành công!"
                                reason = ""
                            } else {
                                errorMessage = "Thất bại: ${result.message()}"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Lỗi: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = reason.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gửi tố cáo")
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            successMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
            }

            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
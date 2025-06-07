package com.example.itforum.user.ReportAccount.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tố cáo tài khoản", color = MaterialTheme.colorScheme.onPrimaryContainer) },
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
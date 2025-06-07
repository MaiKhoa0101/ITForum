package com.example.itforum.user.ReportPost.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.itforum.repository.ReportPostRepository
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.ui.theme.MainTheme
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportPostScreen(
    reporterUserId: String,
    reportedPostId: String,
    onBack: () -> Unit
) {
    val repository = ReportPostRepository(RetrofitInstance.reportPostService)
    val scope = rememberCoroutineScope()

    var reason by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tố cáo bài viết", color = MaterialTheme.colorScheme.onPrimaryContainer) },
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
            Text("Lý do tố cáo:", style = MaterialTheme.typography.titleLarge)

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
                            val result = repository.createReportPost(
                                reportedPostId = reportedPostId,
                                reporterUserId = reporterUserId,
                                reason = reason
                            )
                            if (result.isSuccessful) {
                                successMessage = "✅ Gửi tố cáo thành công!"
                                reason = ""
                            } else {
                                errorMessage = "❌ Lỗi: ${result.message()}"
                            }
                        } catch (e: Exception) {
                            errorMessage = "❌ Lỗi hệ thống: ${e.message}"
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

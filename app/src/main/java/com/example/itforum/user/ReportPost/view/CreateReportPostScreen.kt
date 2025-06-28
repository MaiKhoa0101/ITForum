package com.example.itforum.user.ReportPost.view

import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.itforum.repository.ReportPostRepository
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.ui.theme.MainTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportPostScreen(
    sharedPreferences: SharedPreferences,
    reportedPostId: String,
    onDismiss: () -> Unit
) {
    val repository = ReportPostRepository(RetrofitInstance.reportPostService)
    val scope = rememberCoroutineScope()

    var reason by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val suggestionReasons = listOf(
        "Nội dung không phù hợp",
        "Spam hoặc quảng cáo",
        "Ngôn ngữ xúc phạm",
        "Thông tin sai sự thật",
        "Hình ảnh nhạy cảm",
        "Tuyên truyền thù địch",
        "Lừa đảo hoặc gian lận",
        "Vi phạm quy định cộng đồng"
    )

    ModalBottomSheet(
        modifier = Modifier.padding(top = 50.dp),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Lý do tố cáo:", style = MaterialTheme.typography.titleLarge)

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
                            val result = repository.createReportPost(
                                reportedPostId = reportedPostId,
                                reporterUserId = sharedPreferences.getString("userId", null) ?: "",
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

@Composable
fun ReportPostDialog(
    sharedPreferences: SharedPreferences,
    reportedPostId: String,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            CreateReportPostScreen(
                sharedPreferences = sharedPreferences,
                reportedPostId = reportedPostId,
                onDismiss = onDismissRequest
            )
        }
    }
}
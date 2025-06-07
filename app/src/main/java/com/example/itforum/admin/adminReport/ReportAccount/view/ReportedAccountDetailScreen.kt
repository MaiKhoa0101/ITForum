package com.example.itforum.admin.adminReport.ReportAccount.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.itforum.admin.adminReport.ReportAccount.viewmodel.ReportedAccountDetailViewModel
import com.example.itforum.admin.adminReport.ReportAccount.viewmodel.ReportedAccountDetailViewModelFactory
import com.example.itforum.admin.components.DetailItem
import com.example.itforum.admin.components.DetailScreenLayout
import com.example.itforum.repository.ReportRepository
import com.example.itforum.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun ReportedAccountDetailScreen(
    reportId: String,
    onBack: () -> Unit
) {
    val viewModel: ReportedAccountDetailViewModel = viewModel(
        factory = ReportedAccountDetailViewModelFactory(
            ReportRepository(RetrofitInstance.reportAccountService)
        )
    )

    val snackbarHostState = remember { SnackbarHostState() } // ✅ Snackbar state
    val scope = rememberCoroutineScope()

    LaunchedEffect(reportId) {
        viewModel.loadReportedUserDetail(reportId)
    }

    val data by viewModel.detail.collectAsState()
    var durationInDays by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) } // ✅ Add snackbar host
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (data != null) {
                DetailScreenLayout(title = "Chi tiết tài khoản bị tố cáo", onBack = onBack) {
                    Text("Thông tin tài khoản:", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))

                    DetailItem("ID", data!!.reportedUser._id)
                    DetailItem("Tên", data!!.reportedUser.name)
                    DetailItem("Email", data!!.reportedUser.email)
                    DetailItem("SĐT", data!!.reportedUser.phone)
                    DetailItem("Trạng thái", if (data!!.reportedUser.isBanned) "Đã bị khóa" else "Bình thường")

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Người tố cáo: ${data!!.reporterName}", style = MaterialTheme.typography.titleMedium)
                    DetailItem("Lý do", data!!.reason)
                    DetailItem("Ngày tố cáo", data!!.createdAt)

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = durationInDays,
                        onValueChange = { durationInDays = it },
                        label = { Text("Số ngày khóa") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = {
                                if (durationInDays.isNotBlank()) {
                                    viewModel.banUser(
                                        data!!.reportedUser._id,
                                        durationInDays.toInt()
                                    )
                                    scope.launch {
                                        snackbarHostState.showSnackbar("✅ Khóa thành công")
                                    }
                                }
                            }
                        ) {
                            Text("Khóa tài khoản")
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.unbanUser(data!!.reportedUser._id)
                                scope.launch {
                                    snackbarHostState.showSnackbar("🟢 Bỏ chặn thành công")
                                }
                            }
                        ) {
                            Text("Bỏ chặn")
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.padding(16.dp)) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Đang tải dữ liệu người dùng...")
                }
            }
        }
    }
}



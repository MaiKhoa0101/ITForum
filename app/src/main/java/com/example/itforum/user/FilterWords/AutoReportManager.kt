package com.example.itforum.user.FilterWords

import com.example.itforum.repository.ReportRepository
import com.example.itforum.admin.adminReport.ReportAccount.model.request.BanUserRequest
import com.example.itforum.retrofit.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AutoReportManager {
    private val repository = ReportRepository(RetrofitInstance.reportAccountService)
    private const val REPORT_REASON = "Vi phạm ngôn từ nhiều lần tự tố cáo chính mình"

    fun sendReport(userId: String, violations: List<ViolationEntry>) {
        if (userId.isBlank()) {
            println("❌ Không thể gửi tố cáo: userId bị null hoặc rỗng")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Gửi tố cáo
                val reportResponse = repository.createReportAccount(
                    reportedUserId = userId,
                    reporterUserId = userId,
                    reason = REPORT_REASON
                )

                if (reportResponse.isSuccessful) {
                    println("✅ Gửi báo cáo vi phạm thành công (tự tố cáo)")

                    // ✅ Khóa tài khoản 1 ngày
                    val banResponse = repository.banUser(
                        userId = userId,
                        request = BanUserRequest(durationInDays = 1)
                    )

                    if (banResponse.isSuccessful) {
                        println("✅ Tài khoản $userId đã bị khóa trong 1 ngày")
                    } else {
                        println("❌ Không thể khóa tài khoản: ${banResponse.code()} - ${banResponse.errorBody()?.string()}")
                    }

                } else {
                    println("❌ Gửi báo cáo thất bại: ${reportResponse.code()} - ${reportResponse.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("❌ Lỗi khi gửi tố cáo hoặc khóa tài khoản: ${e.message}")
            }
        }
    }
}

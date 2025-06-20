package com.example.itforum.user.FilterWords

import android.content.Context
import com.example.itforum.admin.adminCrashlytic.UserSession.userId
import com.example.itforum.repository.ReportRepository
import com.example.itforum.retrofit.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AutoReportManager {
    private val repository = ReportRepository(RetrofitInstance.reportAccountService)
    private const val REPORT_REASON = "Vi phạm ngôn từ nhiều lần"

    fun sendReport(userId:String, violations: List<ViolationEntry>) {
        if (userId.isNullOrBlank()) {
            println("Không thể gửi tố cáo: userId bị null hoặc rỗng")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.createReportAccount(
                    reportedUserId = userId,
                    reporterUserId = userId,
                    reason = REPORT_REASON
                )

                if (response.isSuccessful) {
                    println("Gửi báo cáo vi phạm thành công (tự tố cáo)")
                } else {
                    println("Gửi thất bại: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Lỗi gửi tố cáo tự động: ${e.message}")
            }
        }
    }
}

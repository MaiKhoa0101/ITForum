package com.example.itforum.repository

import com.example.itforum.admin.adminReport.ReportAccount.model.request.BanUserRequest
import com.example.itforum.admin.adminReport.ReportAccount.model.request.CreateReportAccountRequest
import com.example.itforum.admin.adminReport.ReportAccount.model.response.BanUserResponse
import com.example.itforum.admin.adminReport.ReportAccount.model.response.ReportedAccountResponse
import com.example.itforum.admin.adminReport.ReportAccount.model.response.ReportedUser
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.service.ReportAccountService
import retrofit2.Response

class ReportRepository(private val service: ReportAccountService) {
    suspend fun getReportedUsers(): Response<List<ReportedUser>> {
        return RetrofitInstance.reportAccountService.getReportedUsers()
    }
    suspend fun getReportedUserDetail(reportId: String): Response<ReportedAccountResponse> {
        return RetrofitInstance.reportAccountService.getReportedUserDetail(reportId)
    }
    suspend fun banUser(userId: String, request: BanUserRequest): Response<BanUserResponse> {
        return service.banUser(userId, request)
    }

    suspend fun updateUser(userId: String, fields: Map<String, Any?>): Response<Unit> {
        return service.updateUser(userId, fields)
    }
    suspend fun createReportAccount(
        reportedUserId: String,
        reporterUserId: String,
        reason: String
    ): Response<Unit> {
        val request = CreateReportAccountRequest(reportedUserId, reporterUserId, reason)
        return service.createReportAccount(request)
    }

}

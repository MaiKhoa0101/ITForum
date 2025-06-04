package com.example.itforum.repository

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

}

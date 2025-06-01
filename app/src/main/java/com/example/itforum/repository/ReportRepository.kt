package com.example.itforum.repository

import com.example.itforum.admin.adminReport.ReportAccount.model.response.ReportedUser
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.service.ReportService
import retrofit2.Response

class ReportRepository(private val service: ReportService) {
    suspend fun getReportedUsers(): Response<List<ReportedUser>> {
        return RetrofitInstance.reportService.getReportedUsers()
    }

}

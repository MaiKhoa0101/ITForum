package com.example.itforum.service
import com.example.itforum.admin.adminReport.ReportAccount.model.response.ReportedAccountResponse
import com.example.itforum.admin.adminReport.ReportAccount.model.response.ReportedUser
import retrofit2.Response
import retrofit2.http.*

interface ReportAccountService {
    @GET("report-account/reported-users")
    suspend fun getReportedUsers(): Response<List<ReportedUser>>
    @GET("report-account/{id}")
    suspend fun getReportedUserDetail(@Path("id") id: String): Response<ReportedAccountResponse>

}
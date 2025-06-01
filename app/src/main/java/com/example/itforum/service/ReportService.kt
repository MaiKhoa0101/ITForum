package com.example.itforum.service
import com.example.itforum.admin.adminReport.ReportAccount.model.request.CreateReportRequest
import com.example.itforum.admin.adminReport.ReportAccount.model.response.ReportResponse
import com.example.itforum.admin.adminReport.ReportAccount.model.response.ReportedUser
import retrofit2.Response
import retrofit2.http.*

interface ReportService {

    @POST("report-account")
    suspend fun createReport(@Body request: CreateReportRequest): Response<ReportResponse>

    @GET("report-account/reported-users")
    suspend fun getReportedUsers(): Response<List<ReportedUser>>


    @GET("report-account/{id}")
    suspend fun getReportById(@Path("id") id: String): Response<ReportResponse>

    @PATCH("report-account/{id}")
    suspend fun updateReport(
        @Path("id") id: String,
        @Body request: CreateReportRequest
    ): Response<ReportResponse>

    @DELETE("report-account/{id}")
    suspend fun deleteReport(@Path("id") id: String): Response<Unit>
}

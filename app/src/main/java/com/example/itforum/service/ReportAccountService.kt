package com.example.itforum.service
import com.example.itforum.admin.adminReport.ReportAccount.model.request.BanUserRequest
import com.example.itforum.admin.adminReport.ReportAccount.model.request.CreateReportAccountRequest
import com.example.itforum.admin.adminReport.ReportAccount.model.response.BanUserResponse
import com.example.itforum.admin.adminReport.ReportAccount.model.response.ReportedAccountResponse
import com.example.itforum.admin.adminReport.ReportAccount.model.response.ReportedUser
import retrofit2.Response
import retrofit2.http.*

interface ReportAccountService {
    @GET("report-account/reported-users")
    suspend fun getReportedUsers(): Response<List<ReportedUser>>
    @GET("report-account/{id}")
    suspend fun getReportedUserDetail(@Path("id") id: String): Response<ReportedAccountResponse>
    @PATCH("user/ban/{id}")
    suspend fun banUser(
        @Path("id") userId: String,
        @Body request: BanUserRequest
    ): Response<BanUserResponse>

    @PUT("user/update/{id}")
    suspend fun updateUser(
        @Path("id") userId: String,
        @Body fields: Map<String, @JvmSuppressWildcards Any?>
    ): Response<Unit>
    @POST("report-account")
    suspend fun createReportAccount(
        @Body request: CreateReportAccountRequest
    ): Response<Unit>


}
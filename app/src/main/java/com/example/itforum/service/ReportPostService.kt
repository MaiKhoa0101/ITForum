package com.example.itforum.service

import com.example.itforum.admin.adminReport.ReportPost.model.request.ReportedPost
import com.example.itforum.admin.adminReport.ReportPost.model.response.ReportedPostDetailResponse
import com.example.itforum.admin.adminReport.ReportPost.model.response.ReportedPostList
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportPostService {

    @GET("report-post")
    suspend fun getAllReportedPosts(): Response<List<ReportedPostList>>

    @GET("report-post/{id}")
    suspend fun getPostDetailByReportId(@Path("id") id: String): Response<ReportedPostDetailResponse>

    @POST("report-post")
    suspend fun createReportPost(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>
}

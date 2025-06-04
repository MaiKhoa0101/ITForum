package com.example.itforum.service

import com.example.itforum.admin.adminReport.ReportPost.model.request.ReportedPost
import com.example.itforum.admin.adminReport.ReportPost.model.request.ReportedPostDetail
import com.example.itforum.admin.adminReport.ReportPost.model.request.ReportedPostDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ReportPostService {
    @GET("report-post")
    suspend fun getAllReportedPosts(): List<ReportedPost>
    @GET("report-post/{id}")
    suspend fun getPostDetailByReportId(@Path("id") id: String): ReportedPostDetailResponse


}
package com.example.itforum.service

import com.example.itforum.admin.adminReport.ReportPost.model.request.ReportedPost
import retrofit2.http.GET

interface ReportPostService {
    @GET("report-post")
    suspend fun getAllReportedPosts(): List<ReportedPost>
}
package com.example.itforum.repository

import com.example.itforum.admin.adminReport.ReportPost.model.request.ReportedPost
import com.example.itforum.admin.adminReport.ReportPost.model.response.ReportedPostDetailResponse
import com.example.itforum.admin.adminReport.ReportPost.model.response.ReportedPostList
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.service.ReportPostService
import retrofit2.Response

class ReportPostRepository(private val service: ReportPostService) {
    suspend fun getAllReportedPosts(): Response<List<ReportedPostList>> {
        return RetrofitInstance.reportPostService.getAllReportedPosts()
    }
    suspend fun getPostDetailByReportId(id: String): Response<ReportedPostDetailResponse>{
        return RetrofitInstance.reportPostService.getPostDetailByReportId(id)
    }
    suspend fun createReportPost(
        reportedPostId: String,
        reporterUserId: String,
        reason: String
    ): Response<Unit> {
        val body = mapOf(
            "reportedPostId" to reportedPostId,
            "reporterUserId" to reporterUserId,
            "reason" to reason
        )
        return service.createReportPost(body)
    }


}

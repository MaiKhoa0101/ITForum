package com.example.itforum.repository

import com.example.itforum.admin.adminReport.ReportPost.model.request.ReportedPost
import com.example.itforum.service.ReportPostService

class ReportPostRepository(private val service: ReportPostService) {
    suspend fun getAllReportedPosts(): List<ReportedPost> {
        return service.getAllReportedPosts()
    }
}

package com.example.itforum.admin.adminReport.ReportPost.model.request

data class ReportedPostDetailResponse(
    val _id: String, // ID của report
    val reason: String,
    val createdAt: String,
    val reportedPost: ReportedPostDetail,
    val reporterUser: ReporterUser
)

data class ReporterUser(
    val _id: String,
    val name: String,
    val email: String
)
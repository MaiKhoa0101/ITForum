package com.example.itforum.admin.adminReport.ReportPost.model.response

data class ReportedPostDetailResponse(
    val _id: String,
    val reason: String,
    val createdAt: String,
    val reportedPost: ReportedPostDetail,
    val reporterUser: ReporterUser
)

data class ReportedPostDetail(
    val _id: String,
    val userId: String,
    val title: String,
    val content: String,
    val imageUrl: String?,
    val tags: List<String>,
    val isPublished: Boolean,
    val totalUpvotes: Int,
    val totalDownvotes: Int,
    val createdAt: String,
    val updatedAt: String
)

data class ReporterUser(
    val _id: String,
    val name: String,
    val email: String
)

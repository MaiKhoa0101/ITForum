package com.example.itforum.admin.adminReport.ReportPost.model.response

data class AiAnalysis(
    val violationPercentage: Int,
    val reason: String,
    val shouldBan: Boolean
)
data class ReportedPostDetailResponse(
    val _id: String,
    val reason: String,
    val createdAt: String,
    val reportedPost: ReportedPostDetail,
    val reporterUser: ReporterUser,
    val aiAnalysis: AiAnalysis?
)

data class ReportedPostDetail(
    val _id: String,
    val userId: String,
    val title: String,
    val content: String,
    val imageUrls: List<String>,
    val videoUrls: List<String>,
    val tags: List<String>,
    val isPublished: String,
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

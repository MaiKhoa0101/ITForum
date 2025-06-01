package com.example.itforum.admin.adminReport.ReportPost.model.request

data class ReportedPost(
    val _id: String,
    val reportedPostId: String?,
    val reporterUserId: String?,
    val reason: String,
    val createdAt: String
)

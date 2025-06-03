package com.example.itforum.admin.adminReport.ReportPost.model.request

data class ReportedPostDetail(
    val _id: String,
    val userId: String,
    val title: String,
    val content: String,
    val imageUrl: String,
    val tags: List<String>,
    val isPublished: String,
    val totalUpvotes: Int,
    val totalDownvotes: Int,
    val createdAt: String,
    val updatedAt: String
)
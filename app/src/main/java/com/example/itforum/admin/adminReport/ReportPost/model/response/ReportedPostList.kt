package com.example.itforum.admin.adminReport.ReportPost.model.response
// biểu diễn danh sách bài báo cáo
data class ReportedPostList(
    val _id: String,
    val reportedPostId: String,
    val reportedPostTitle: String,
    val reason: String,
    val createdAt: String
)


package com.example.itforum.admin.adminReport.ReportPost.model.request


data class ReportedPost(
    val _id: String,
    val reportedPostId: SimplePostInfo?,
    val reporterUserId: SimpleUserInfo?,
    val reason: String,
    val createdAt: String
)

data class SimplePostInfo(val _id: String)
data class SimpleUserInfo(val _id: String)



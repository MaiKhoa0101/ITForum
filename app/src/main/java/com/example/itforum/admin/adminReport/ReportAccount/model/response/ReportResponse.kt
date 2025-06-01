package com.example.itforum.admin.adminReport.ReportAccount.model.response

data class ReportResponse(
    val _id: String,
    val reportedUserId: String,
    val reporterUserId: String,
    val reason: String,
    val createdAt: String,
    val updatedAt: String
)

package com.example.itforum.admin.adminReport.ReportAccount.model.response

data class ReportedUser(
    val userId: String,
    val username: String,
    val email: String,
    val reportCount: Int
)

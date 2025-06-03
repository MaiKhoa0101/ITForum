package com.example.itforum.admin.adminReport.ReportAccount.model.response

data class ReportedAccountUIModel(
    val reportedUserId: String,
    val reportedUserName: String,
    val email: String,
    val phone: String,
    val isBanned: Boolean,
    val reporterName: String,
    val reason: String,
    val createdAt: String
)

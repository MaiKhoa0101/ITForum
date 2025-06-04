package com.example.itforum.admin.adminReport.ReportAccount.model.response
//lấy chi tiết thông tin người dùng bị tố cáo
data class ReportedAccountResponse(
    val reportedUserId: String,
    val reportedUserName: String,
    val email: String,
    val phone: String,
    val isBanned: Boolean,
    val reporterName: String,
    val reason: String,
    val createdAt: String
)

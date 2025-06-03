package com.example.itforum.admin.adminReport.ReportAccount.model.response
// lấy thông tin người dùng bị tố cáo
data class ReportedUser(
    val _id: String,
    val userId: String,
    val username: String,
    val email: String,
    val reportCount: Int
)

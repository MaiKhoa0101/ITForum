package com.example.itforum.admin.adminReport.ReportAccount.model.response
//lấy chi tiết thông tin người dùng bị tố cáo
data class ReportedUserDetail(
    val _id: String,
    val name: String,
    val email: String,
    val phone: String,
    val isBanned: Boolean
)

data class ReportedAccountResponse(
    val _id: String,
    val reportedUser: ReportedUserDetail, // 🔁 đổi kiểu
    val reporterName: String,
    val reason: String,
    val createdAt: String
)


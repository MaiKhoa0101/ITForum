package com.example.itforum.admin.adminReport.ReportAccount.model.response

import com.example.itforum.admin.components.TableRowConvertible

// lấy thông tin người dùng bị tố cáo
data class ReportedUser(
    val _id: String,
    val userId: String,
    val username: String,
    val email: String,
    val reportCount: Int
): TableRowConvertible {
    override fun toTableRow(): List<String> {
        return listOf(_id, userId, username, email, reportCount.toString())
    }
}

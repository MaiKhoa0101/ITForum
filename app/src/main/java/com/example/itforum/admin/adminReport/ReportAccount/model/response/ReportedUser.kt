package com.example.itforum.admin.adminReport.ReportAccount.model.response

import com.example.itforum.admin.components.TableRowConvertible

// lấy thông tin người dùng bị tố cáo
data class ReportedUser(
    val _id: String,
    val reportedUserId: String,
    val reporterUserId: String,
    val reason: String,
    val createdAt: String
) : TableRowConvertible {
    override fun toTableRow(): List<String?> {
        return listOf(_id, reportedUserId, reporterUserId, reason, createdAt)
    }
}

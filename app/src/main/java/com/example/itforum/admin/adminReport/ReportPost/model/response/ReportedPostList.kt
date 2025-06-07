package com.example.itforum.admin.adminReport.ReportPost.model.response

import com.example.itforum.admin.components.TableRowConvertible

// biểu diễn danh sách bài báo cáo
data class ReportedPostList(
    val _id: String,
    val reportedPostId: String,
    val reportedPostTitle: String,
    val reason: String,
    val createdAt: String
): TableRowConvertible {
    override fun toTableRow(): List<String> {
        return listOf(_id, reportedPostId, reportedPostTitle, reason, createdAt)
    }
}


package com.example.itforum.admin.adminReport.ReportPost

import com.example.itforum.admin.adminReport.ReportPost.model.request.ReportedPost


fun convertReportedPostsToRows(posts: List<ReportedPost>): List<List<String>> {
    return posts.map {
        listOf(
            it._id,
            it.reportedPostId ?: "Không có ID bài viết",
            it.reporterUserId ?: "Không có ID người báo cáo",
            it.reason,
            it.createdAt)
    }
}
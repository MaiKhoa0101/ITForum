package com.example.itforum.admin.adminReport.ReportPost.model.request

import com.example.itforum.admin.adminReport.common.model.BaseReport
import com.example.itforum.admin.adminReport.common.model.ReportType
import com.example.itforum.admin.adminReport.common.model.Reportable


data class ReportedPost(
    override val _id: String,
    override val reportedEntityId: String,
    override val reporterUserId: String,
    override val reason: String,
    override val createdAt: String
):BaseReport(), Reportable {
    override fun getType() = ReportType.POST
    override fun toRow(): List<String> = listOf(_id, reportedEntityId, reporterUserId, reason, createdAt)

}





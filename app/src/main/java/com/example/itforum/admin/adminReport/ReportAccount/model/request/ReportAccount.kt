package com.example.itforum.admin.adminReport.ReportAccount.model.request

import com.example.itforum.admin.adminReport.common.model.BaseReport
import com.example.itforum.admin.adminReport.common.model.ReportType
import com.example.itforum.admin.adminReport.common.model.Reportable
data class ReportAccount(
    override val _id: String,
    override val reportedEntityId: String,
    override val reporterUserId: String,
    override val reason: String,
    override val createdAt: String
) : BaseReport(), Reportable {

    override fun getType() = ReportType.ACCOUNT

    override fun toRow(): List<String> =
        listOf(_id, reportedEntityId, reporterUserId, reason, createdAt)
}

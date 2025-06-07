package com.example.itforum.admin.adminReport.common.model

interface Reportable {
    val reportedEntityId: String
    fun getType(): ReportType
    fun toRow(): List<String>
}
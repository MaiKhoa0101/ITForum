package com.example.itforum.admin.adminReport.ReportAccount.model.request

data class CreateReportAccountRequest(
    val reportedUserId: String,
    val reporterUserId: String,
    val reason: String
)

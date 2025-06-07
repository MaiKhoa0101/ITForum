package com.example.itforum.admin.adminReport.common.repository

interface ReportRepository <T> {
    suspend fun getAll(): List<T>
    suspend fun getByReportId(id: String): T?

}
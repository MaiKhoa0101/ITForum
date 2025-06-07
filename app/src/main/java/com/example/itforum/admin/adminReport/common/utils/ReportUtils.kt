package com.example.itforum.admin.adminReport.common.utils

import com.example.itforum.admin.adminReport.common.model.Reportable

fun <T: Reportable> convertReportToRow(report: List<T>): List<List<String>> {
    return report.map { it.toRow() }
}
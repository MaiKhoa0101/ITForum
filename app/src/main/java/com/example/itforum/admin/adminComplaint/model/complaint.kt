package com.example.itforum.admin.adminComplaint.model

import com.example.itforum.admin.adminAccount.TableRowConvertible
import java.time.LocalDate

data class complaint(
    val id: Int,
    val senderName: String,
    val targetName: String,
    val reason: String,
    val sendDate: LocalDate,
    val status: String

): TableRowConvertible {
    override fun toTableRow(): List<String> {
        return listOf(id.toString(), senderName, targetName, reason, sendDate.toString(), status)
    }
}
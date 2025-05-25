package com.example.itforum.Model

import com.example.itforum.admin.adminAccount.TableRowConvertible
import java.time.LocalDate

data class account(
    val id: Int,
    val userName: String,
    val email: String,
    val sdt: String,
    val createdDate: LocalDate
): TableRowConvertible {
    override fun toTableRow(): List<String> {
        return listOf(id.toString(), userName, email, sdt, createdDate.toString())
    }
}

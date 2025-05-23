    package com.example.itforum.Model

    import java.time.LocalDate

    data class account(
        val id: Int,
        val userName: String,
        val email: String,
        val sdt: String,
        val createdDate: LocalDate
    )

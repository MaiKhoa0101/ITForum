package com.example.itforum.user.FilterWords

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ViolationEntry(val content:String, val timestamp:String)
object ViolationLogger{
    private val userViolation= mutableMapOf<String,MutableList<ViolationEntry>>()
    private const val LIMIT=3
    fun log(userId: String, content: String) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault()).format(
            Date()
        )
        val entry = ViolationEntry(content, timestamp)
        val list = userViolation.getOrPut(userId) { mutableListOf() }
        list.add(entry)

        if (list.size >= LIMIT) {
            AutoReportManager.sendReport(
                userId = userId,
                violations = list
            )
            userViolation.remove(userId)
        }
    }

}
package com.example.itforum.admin.adminAnalytics

data class ScreenEvent(
    val screen_name: String?,
    val view_count: Int,
    val total_duration_seconds: Long
)

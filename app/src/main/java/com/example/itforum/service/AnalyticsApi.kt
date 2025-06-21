package com.example.itforum.service

import com.example.itforum.user.Analytics.ScreenStat
import retrofit2.http.GET

interface AnalyticsApi {
    @GET("screen-stats")  // 👈 chính là endpoint bạn định nghĩa trong Node.js
    suspend fun getScreenStats(): List<ScreenStat>
}


package com.example.itforum.service

import com.example.itforum.admin.adminAnalytics.ScreenEvent
import retrofit2.http.GET

interface AnalyticsApi {
    @GET("api/events")
    suspend fun getScreenEvents(): List<ScreenEvent>
}

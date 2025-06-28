package com.example.itforum.service

import com.example.itforum.user.modelData.request.NotificationRequest
import com.example.itforum.user.modelData.response.AllNotificationResponse
import com.example.itforum.user.modelData.response.Notification
import com.example.itforum.user.modelData.response.NotificationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationService {
    @GET("notification/find/{userId}")
    suspend fun getNotifications(@Path("userId") userId: String): Response<NotificationResponse>

    @PATCH("notification/readNotification/{id}")
    suspend fun readNotification(@Path("id") id: String): Response<String>

    @GET("notification/find/all")
    suspend fun getAllNotifications(): Response<AllNotificationResponse>

    @POST("notification/create/topic")
    suspend fun createNotificationTopic(@Body notification: NotificationRequest): Response<String>

    @GET("notification/find/one/{id}")
    suspend fun getNotificationById(@Path("id") id: String): Response<Notification>
}
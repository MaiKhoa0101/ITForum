package com.example.itforum.user.modelData.response

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("_id") val id: String,
    val title: String,
    val content: String,
    val postId: String,
    val isRead: Boolean,
    val createdAt: String,
    val updatedAt: String

)

data class NotificationResponse(
    val message: String,
    val data: List<Notification>
)
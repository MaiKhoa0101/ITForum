package com.example.itforum.user.modelData.request

import com.google.gson.annotations.SerializedName

data class NotificationRequest(
    @SerializedName("receiverId")
    val userReceiveNotificationId: String,
    val title: String,
    val content: String,
    val userId: String,
)
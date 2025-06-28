package com.example.itforum.user.modelData.response

import com.example.itforum.admin.components.TableRowConvertible
import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("_id") val id: String,
    val title: String,
    val userReceiveNotificationId: String? = null,
    val content: String,
    val postId: String? = null,
    val isRead: Boolean,
    val createdAt: String,
    val updatedAt: String
) : TableRowConvertible {
    override fun toTableRow(): List<String> {
        return listOf(id, title, content, userReceiveNotificationId?:"Tất cả", postId?:"Không có", createdAt)
    }
}

data class NotificationResponse(
    val message: String,
    val data: List<Notification>
)

data class AllNotificationResponse(
    val message: String,
    @SerializedName("listNotification")
    val listNotification: List<Notification>
)
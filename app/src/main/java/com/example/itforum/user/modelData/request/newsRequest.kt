package com.example.itforum.user.modelData.request

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

data class NewsRequest(
    val adminId: String,
    val title: String,
    val content: String,
    val img: Uri?
)

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey val id: String,
    val adminId: String,
    val title: String,
    val content: String,
    val img: String?,
    val createdAt: String,
    val isSynced: Boolean = true // dùng để đánh dấu cần đồng bộ hay không
)
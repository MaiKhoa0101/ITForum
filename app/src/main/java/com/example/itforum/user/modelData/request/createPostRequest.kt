package com.example.itforum.user.modelData.request

import android.net.Uri

data class CreatePostRequest(
    val userId: String?,
    val title: String?,
    val content: String?,
    val tags: List<String?>? = emptyList(),
    val isPublished: String?,
    val imageUrls: List<Uri>? = emptyList(),
    val videoUrls: List<Uri>? = emptyList()
)
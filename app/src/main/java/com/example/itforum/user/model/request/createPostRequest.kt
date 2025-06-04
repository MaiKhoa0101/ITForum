package com.example.itforum.user.model.request

import android.net.Uri

data class CreatePostRequest(
    val userId: String?,
    val title: String?,
    val content: String?,
    val tags: List<String?>,
    val isPublished: String?,
    val imageUrls: List<Uri>? = emptyList()
)
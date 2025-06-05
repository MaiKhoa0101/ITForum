package com.example.itforum.user.modelData.request

import android.net.Uri

data class CreatePostRequest(
    val imageUrl: Uri?,
    val userId: String,
    val title: String,
    val content: String,
    val tags: List<String?>,
    val isPublished: String
)
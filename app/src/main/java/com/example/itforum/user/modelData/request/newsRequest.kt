package com.example.itforum.user.modelData.request

import android.net.Uri

data class NewsRequest(
    val adminId: String,
    val title: String,
    val content: String,
    val img: Uri?
)


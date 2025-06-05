package com.example.itforum.user.modelData.request

data class NewsRequest(
    val adminId: String,
    val title: String,
    val content: String,
    val img: String
)


package com.example.itforum.user.model.request

data class NewsRequest(
    val adminId: String,
    val title: String,
    val content: String,
    val img: String
)


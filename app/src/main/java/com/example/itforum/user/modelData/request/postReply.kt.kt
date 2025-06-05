package com.example.itforum.user.model.request

data class PostReply(
    val userId: String,
    val commentId: String,
    val content: String
)
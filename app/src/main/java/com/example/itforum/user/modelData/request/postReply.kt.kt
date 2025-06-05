package com.example.itforum.user.modelData.request

data class PostReply(
    val userId: String,
    val commentId: String,
    val content: String
)
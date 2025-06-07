package com.example.itforum.user.modelData.request

data class PostComment(
    var userId: String,
    val postId: String,
    val content: String,
)
package com.example.itforum.user.model.request

data class PostComment(
    var userId: String,
    val postId: String,
    val content: String,
)
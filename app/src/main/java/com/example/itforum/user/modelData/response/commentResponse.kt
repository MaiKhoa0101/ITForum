package com.example.itforum.user.modelData.response

data class Comment(
    val content: String,
    val userId: String,
    val time: String,
    val id: String,
    val totalReply : Int,
    val avatar : String?,
    val userName : String?
)

data class CommentResponse(
    val comments: List<Comment>,
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int
)
package com.example.itforum.user.modelData.response

data class Reply(
    val content: String,
    val userId: String,
    val time: String,
    val avatar : String?,
    val userName : String?
)

data class ReplyResponse(
    val replies: List<Reply>,
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int
)
package com.example.itforum.user.model.request

data class GetPostRequest(
    val userId: String? = null,
    val title: String? = null,
    val tags: List<String>? = null,
    val page: Int? = null,
    val limit: Int? = null
)

package com.example.itforum.user.post.model

data class PostModel(
    val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val imageUrl: String,
    val tags: List<String> = emptyList(),
    val isPublished: String,
    val totalUpvotes: Int,
    val totalDownvotes: Int,
    val __v: Int,
    val readTime: Int = 100 // default value
)
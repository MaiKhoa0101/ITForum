package com.example.itforum.user.post

data class PostModel(
    val id: Int,
    val author: String,
    val readTime: String,
    val date: String,
    val title: String,
    val imageRes: Int,
    val likes: Int
)

package com.example.itforum.user.modelData.response

import com.google.gson.annotations.SerializedName

data class News(
    @SerializedName("_id")
    val id: String,
    val adminId: String,
    val title: String,
    val content: String,
    val img: String,
    val createdAt: String
)

data class NewsResponse(
    val message: String,
    val listNews: List<News>
)
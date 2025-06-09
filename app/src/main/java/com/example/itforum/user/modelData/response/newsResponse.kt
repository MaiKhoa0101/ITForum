package com.example.itforum.user.modelData.response

import com.example.itforum.admin.components.TableRowConvertible
import com.google.gson.annotations.SerializedName

data class News(
    @SerializedName("_id")
    val id: String,
    val adminId: String,
    val title: String,
    val content: String,
    val img: String,
    val createdAt: String
): TableRowConvertible {
    override fun toTableRow(): List<String> {
        return listOf(id, adminId, title, content, createdAt)
    }
}

data class NewsResponse(
    val message: String,
    val listNews: List<News>
)

data class CreateNewsResponse(
    val message: String,
    val news: News
)
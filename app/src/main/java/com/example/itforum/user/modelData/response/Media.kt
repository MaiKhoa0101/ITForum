package com.example.itforum.user.modelData.response

data class MediaItem(
    val url: String,
    val type: MediaType
)

enum class MediaType {
    IMAGE,
    VIDEO
}
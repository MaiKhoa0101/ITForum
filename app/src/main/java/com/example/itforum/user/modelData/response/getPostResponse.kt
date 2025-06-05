package com.example.itforum.user.modelData.response

import com.google.gson.annotations.SerializedName

data class PostResponse(
    @SerializedName("_id")
    val id: String? = null,
    val userId: String? = null,
    val title: String? = null,
    val content: String? = null,
    val imageUrl: String? = null,
    val tags: List<String>? = null,
    val isPublished: String? = null,
    val totalUpvotes: Int? = null,
    val totalDownvotes: Int? = null,
    val __v: Int? = null,
    val createdAt : String? = null,
    val userName : String? = null
)
data class PostListResponse(
    val posts: List<PostResponse>? = null,
    val total: Int? = null,
    val page: Int? = null,
    val limit: Int? = null,
    val totalPages: Int? = null
)
data class PostWithVote(
    val post: PostResponse,
    val vote: GetVoteResponse?
)
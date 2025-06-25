package com.example.itforum.user.modelData.response

import com.google.gson.annotations.SerializedName

data class PostWrapperResponse(
    val post: PostResponse
)

data class PostResponse(
    @SerializedName("_id")
    val id: String? = null,
    val userId: String? = null,
    var title: String? = null,
    val content: String? = null,
    val imageUrls: List<String>? = null,
    val videoUrls: List<String>? = null,
    val tags: List<String>? = null,
    val isPublished: String? = null,
    var totalUpvotes: Int? = null,
    var totalDownvotes: Int? = null,
    val __v: Int? = null,
    val createdAt : String? = null,
    val userName : String? = null,
    val avatar : String? = null
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
    val vote: GetVoteResponse?,
    var isBookMark: Boolean = false
)
data class PostListWrapper(
    @SerializedName("listPost")
    val listPost: List<PostResponse>
)
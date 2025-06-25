package com.example.itforum.user.modelData.response

import com.example.itforum.admin.components.TableRowConvertible
import com.example.itforum.user.post.getTimeAgo
import com.google.gson.annotations.SerializedName


data class Post(
    @SerializedName("post")
    val post: PostResponse,
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
    val isHidden: Boolean? = null,
    val avatar: String? = null,
): TableRowConvertible {
    override fun toTableRow(): List<String?> {
        return listOf(id, userId, title, content,
            tags?.joinToString(", "),
            if(isPublished == "public") "Công khai" else "Riêng tư",
            createdAt,
            if(isHidden == true) "Ẩn" else "Hiển thị")
    }
}
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


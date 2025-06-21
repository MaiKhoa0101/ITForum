package com.example.itforum.user.modelData.response

import com.google.gson.annotations.SerializedName

data class VoteResponse(
    val statusCode: Int? = null,
    val message: String? = null,
    val data: VoteData? = null
)

data class VoteData(
    val success: Boolean? = null,
    val upvotes: Int? = null,
    val downvotes: Int? = null,
    val userVote: String? = null
)

data class Vote(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("postId")
    val postId: String? = null
)

data class VoteListWrapper(
    @SerializedName("listVote")
    val listVote: List<Vote>
)
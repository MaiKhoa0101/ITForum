package com.example.itforum.user.modelData.response

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
package com.example.itforum.user.model.response

data class GetVoteResponse(
    val statusCode: Int? = null,
    val message: String? = null,
    val data: GetVoteData? = null
)

data class GetVoteData(
    val userVote: String? = null,
    val upVoteData: VoteDetail? = null,
    val downVoteData: VoteDetail? = null
)

data class VoteDetail(
    val userId: List<String>? = null,
    val total: Int? = null
)
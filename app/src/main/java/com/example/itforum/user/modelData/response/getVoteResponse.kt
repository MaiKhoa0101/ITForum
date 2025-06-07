package com.example.itforum.user.modelData.response

data class GetVoteResponse(
    var statusCode: Int? = null,
    var message: String? = null,
    var data: GetVoteData? = null
)

data class GetVoteData(
    var userVote: String? = null,
    var upVoteData: VoteDetail? = null,
    var downVoteData: VoteDetail? = null
)

data class VoteDetail(
    var userId: List<String>? = null,
    var total: Int? = null
)
package com.example.itforum.user.effect.model


import com.example.itforum.user.modelData.response.PostWithVote


sealed class UiStatePost {
    object Loading : UiStatePost()
    data class SuccessWithVotes(val posts: List<PostWithVote>) : UiStatePost()
    data class Error(val message: String) : UiStatePost()
    object Idle : UiStatePost()


}

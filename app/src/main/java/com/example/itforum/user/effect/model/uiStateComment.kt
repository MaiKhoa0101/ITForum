package com.example.itforum.user.effect.model

import com.example.itforum.user.model.response.Comment
import com.example.itforum.user.model.response.CommentResponse
import com.example.itforum.user.model.response.Reply
import com.example.itforum.user.model.response.ReplyResponse

sealed class UiStateComment {
    data class SuccessFetchComment(val comments: List<Comment>) : UiStateComment()
    data class SuccessFetchReply(val replies: List<Reply>) : UiStateComment()
    data class Error(val message: String) : UiStateComment()
    object Loading : UiStateComment()
    object Idle : UiStateComment()
}
package com.example.itforum.user.post.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.effect.model.UiStateComment
import com.example.itforum.user.modelData.request.PostComment
import com.example.itforum.user.modelData.request.PostReply
import com.example.itforum.user.modelData.response.Comment
import com.example.itforum.user.modelData.response.CommentResponse
import com.example.itforum.user.modelData.response.Reply
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommentViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiStateComment>(UiStateComment.Loading)
    val uiState: StateFlow<UiStateComment> = _uiState

    private var userId: String? = sharedPreferences.getString("userId", null)
    private var allComment = mutableListOf<Comment>()
    private var allReply = mutableListOf<Reply>()

    fun fetchComment(postId: String) {
        viewModelScope.launch {
            _uiState.value = UiStateComment.Loading
            try {
                val res = RetrofitInstance.postService.getCommentData(postId, 1, 10)
                if (res.isSuccessful && res.body() != null) {
                    val comments = res.body()?.comments ?: emptyList()
                    allComment.clear()
                    allComment.addAll(comments)
                    Log.d("comments", comments.toString())
                    _uiState.value = UiStateComment.SuccessFetchComment(allComment)
                } else {
                    _uiState.value = UiStateComment.Error(res.message())
                }
            } catch (e: Exception) {
                Log.e("CommentViewModel", "fetchComment error", e)
                _uiState.value = UiStateComment.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchReply(commentId: String, onLoaded: (List<Reply>) -> Unit) {
        viewModelScope.launch {
            try {
                val res = RetrofitInstance.postService.getRepliesData(commentId, 1, 3)
                if (res.isSuccessful && res.body() != null) {
                    val replies = res.body()?.replies ?: emptyList()
                    onLoaded(replies)
                } else {
                    onLoaded(emptyList())
                }
            } catch (e: Exception) {
                onLoaded(emptyList())
            }
        }
    }

    fun postComment(postId: String, content: String, onSuccess: (Comment) -> Unit) {
        viewModelScope.launch {
            val postComment = PostComment(userId = userId.toString(), postId = postId, content = content)
            try {
                val res = RetrofitInstance.postService.postComment(postComment)
                if (res.isSuccessful) {
                    Log.d("Post cmt", res.body().toString())

                    // Fetch the latest comment after successful post
                    val latestComment = fetchLatestComment(postId)
                    if (latestComment != null) {
                        onSuccess(latestComment)
                    }
                }
            } catch (e: Exception) {
                Log.e("Post cmt", "Error: ${e.message}")
            }
        }
    }

    // Fixed postReply function
    fun postReply(commentId: String, content: String, onSuccess: (Reply) -> Unit) {
        viewModelScope.launch {
            val replyData = PostReply(userId.toString(), commentId, content)
            try {
                val res = RetrofitInstance.postService.postReply(replyData)
                if (res.isSuccessful) {
                    Log.d("Post reply", res.body().toString())

                    // Fetch the latest reply after successful post
                    val latestReply = fetchLatestReply(commentId)
                    if (latestReply != null) {
                        onSuccess(latestReply)
                    }
                }
            } catch (e: Exception) {
                Log.e("Post reply", "Error: ${e.message}")
            }
        }
    }

    private suspend fun fetchLatestComment(postId: String): Comment? {
        return try {
            val res = RetrofitInstance.postService.getCommentData(postId, 1, 1)
            if (res.isSuccessful) {
                val comments = res.body()?.comments
                if (!comments.isNullOrEmpty()) {
                    Log.d("latest comment", comments.first().toString())
                    comments.first()
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("latest comment", "Exception: ${e.message}")
            null
        }
    }

    // Fixed fetchLatestReply function
    private suspend fun fetchLatestReply(commentId: String): Reply? {
        return try {
            val res = RetrofitInstance.postService.getRepliesData(commentId, 1, 1)
            if (res.isSuccessful) {
                val replies = res.body()?.replies
                if (!replies.isNullOrEmpty()) {
                    Log.d("latest reply", replies.first().toString())
                    replies.first()
                } else {
                    Log.d("latest reply", "No replies found")
                    null
                }
            } else {
                Log.e("latest reply", "API call failed: ${res.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("latest reply", "Exception: ${e.message}")
            null
        }
    }
}
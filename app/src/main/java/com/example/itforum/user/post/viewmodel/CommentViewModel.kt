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
import com.example.itforum.user.modelData.response.PostResponse
import com.example.itforum.user.modelData.response.Reply
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommentViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private var userId: String? = sharedPreferences.getString("userId", null)

    // Loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSubmittingComment = MutableStateFlow(false)
    val isSubmittingComment: StateFlow<Boolean> = _isSubmittingComment

    private val _isSubmittingReply = MutableStateFlow(false)
    val isSubmittingReply: StateFlow<Boolean> = _isSubmittingReply

    // Comments data
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchComment(postId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val res = RetrofitInstance.postService.getCommentData(postId, 1, 10)
                if (res.isSuccessful && res.body() != null) {
                    val comments = res.body()?.comments ?: emptyList()
                    _comments.value = comments
                    Log.d("comments", comments.toString())
                } else {
                    _error.value = res.message()
                }
            } catch (e: Exception) {
                Log.e("CommentViewModel", "fetchComment error", e)
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchReply(commentId: String, onLoaded: (List<Reply>) -> Unit) {
        viewModelScope.launch {
            try {
                val res = RetrofitInstance.postService.getRepliesData(commentId, 1, 10)
                if (res.isSuccessful && res.body() != null) {
                    val replies = res.body()?.replies ?: emptyList()
                    onLoaded(replies)
                } else {
                    onLoaded(emptyList())
                }
            } catch (e: Exception) {
                Log.e("CommentViewModel", "fetchReply error", e)
                onLoaded(emptyList())
            }
        }
    }

    fun postComment(postId: String, content: String, onSuccess: (Comment) -> Unit) {
        viewModelScope.launch {
            _isSubmittingComment.value = true
            val postComment = PostComment(userId = userId.toString(), postId = postId, content = content)
            try {
                val res = RetrofitInstance.postService.postComment(postComment)
                if (res.isSuccessful) {
                    Log.d("Post cmt", res.body().toString())

                    // Get the response comment from server

                    val newComment = fetchLatestComment(postId)

                    if (newComment != null) {
                        // Add to local comments list immediately
                        val currentComments = _comments.value.toMutableList()
                        currentComments.add(0, newComment)
                        _comments.value = currentComments
                        onSuccess(newComment)
                    }
                } else {
                    Log.e("Post cmt", "Error: ${res.message()}")
                    _error.value = "Failed to post comment: ${res.message()}"
                }
            } catch (e: Exception) {
                Log.e("Post cmt", "Error: ${e.message}")
                _error.value = "Failed to post comment: ${e.message}"
            } finally {
                _isSubmittingComment.value = false
            }
        }
    }

    fun postReply(commentId: String, content: String, onSuccess: (Reply) -> Unit) {
        viewModelScope.launch {
            _isSubmittingReply.value = true
            val replyData = PostReply(userId.toString(), commentId, content)
            try {
                val res = RetrofitInstance.postService.postReply(replyData)
                if (res.isSuccessful) {
                    Log.d("Post reply", res.body().toString())

                    // Get the response reply from server
                    val newReply = fetchLatestReply(commentId)
                    if (newReply != null) {
                        onSuccess(newReply)
                    }
                } else {
                    Log.e("Post reply", "Error: ${res.message()}")
                    _error.value = "Failed to post reply: ${res.message()}"
                }
            } catch (e: Exception) {
                Log.e("Post reply", "Error: ${e.message}")
                _error.value = "Failed to post reply: ${e.message}"
            } finally {
                _isSubmittingReply.value = false
            }
        }
    }


    // Helper function to fetch the latest comment
    private suspend fun fetchLatestComment(postId: String): Comment? {
        return try {
            val res = RetrofitInstance.postService.getCommentData(postId, 1, 1)
            if (res.isSuccessful && res.body() != null) {
                res.body()?.comments?.firstOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("CommentViewModel", "fetchLatestComment error", e)
            null
        }
    }

    // Helper function to fetch the latest reply
    private suspend fun fetchLatestReply(commentId: String): Reply? {
        return try {
            val res = RetrofitInstance.postService.getRepliesData(commentId, 1, 1)
            if (res.isSuccessful && res.body() != null) {
                res.body()?.replies?.firstOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("CommentViewModel", "fetchLatestReply error", e)
            null
        }
    }

    // Function to refresh comments
    fun refreshComments(postId: String) {
        fetchComment(postId)
    }
    fun refreshReplies(commentId: String) {
        fetchComment(commentId)
    }

    // Clear error
    fun clearError() {
        _error.value = null
    }

    // Clear data when needed
    fun clearData() {
        _comments.value = emptyList()
        _error.value = null
        _isLoading.value = false
        _isSubmittingComment.value = false
        _isSubmittingReply.value = false
    }
}
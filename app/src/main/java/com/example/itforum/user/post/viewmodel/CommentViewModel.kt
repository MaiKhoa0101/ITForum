package com.example.itforum.user.post.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.effect.model.UiStateComment
import com.example.itforum.user.model.request.PostComment
import com.example.itforum.user.model.response.Comment
import com.example.itforum.user.model.response.Reply
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
                val res = RetrofitInstance.postService.getCommentData(postId, 1, 3)
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
    fun postComment(postId: String,content : String){
        viewModelScope.launch {
            val postComment = PostComment(userId = userId.toString(),postId = postId,content=content)
            try {
                val res = RetrofitInstance.postService.postComment(postComment)
                if (res.isSuccessful){
                    Log.d("Post cmt",res.body().toString())
                }
            }catch (e : Exception){

            }
        }

    }
}
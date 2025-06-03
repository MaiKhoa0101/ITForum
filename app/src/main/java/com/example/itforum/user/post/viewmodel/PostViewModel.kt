package com.example.itforum.user.post.viewmodel

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.effect.model.UiStatePost
import com.example.itforum.user.model.request.CreatePostRequest
import com.example.itforum.user.model.request.GetPostRequest
import com.example.itforum.user.model.request.RegisterUser
import com.example.itforum.user.model.request.VoteRequest
import com.example.itforum.user.model.response.GetVoteResponse
import com.example.itforum.user.model.response.PostResponse
import com.example.itforum.user.model.response.PostWithVote
import com.example.itforum.user.model.response.VoteResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.Response
import java.io.IOException

class PostViewModel(
    navHostController: NavHostController,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiStatePost>(UiStatePost.Loading)
    val uiState: StateFlow<UiStatePost> = _uiState.asStateFlow()

    private val _uiStateCreate = MutableStateFlow<UiState>(UiState.Idle)
    val uiStateCreate: StateFlow<UiState> = _uiStateCreate.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private var currentPage = 1
    private var canLoadMore = true
    private val allPosts = mutableListOf<PostResponse>()



    private suspend fun getVoteDataByPostId(postId: String?, userId: String?): GetVoteResponse? {
        if (postId.isNullOrEmpty() || userId.isNullOrEmpty()) return null
        return try {
            val response = RetrofitInstance.userService.getVoteData(postId, userId)
            Log.d("vote data",response.body().toString())
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.d("Error","Vote fetch error: ${e.message}")
            null
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun fetchPosts(getPostRequest: GetPostRequest, isRefresh: Boolean = false, isLoadMore: Boolean = false) {
        if (isRefresh) {
            allPosts.clear()
            _isRefreshing.value = true
            currentPage = 1
            canLoadMore = true

        } else if (isLoadMore) {
            if (!canLoadMore || _isLoadingMore.value) return
            _isLoadingMore.value = true
        } else {
            _uiState.value = UiStatePost.Loading
            currentPage = 1
            canLoadMore = true
        }

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.userService.getPost(getPostRequest)
                if (response.isSuccessful && response.body() != null) {
                    val newPosts = response.body()?.posts ?: emptyList()
                    Log.d("post",newPosts.toString());
                    val userId = "683b989ade8e8e2831d3e885"
                    val postsWithVotes = newPosts.map { post ->
                        async {
                            PostWithVote(
                                post = post,
                                vote = getVoteDataByPostId(post.id,userId)
                            )
                        }
                    }.awaitAll()
//                    if (newPosts.size < 3) {
//                        canLoadMore = false
//                    }

                        if (isRefresh || isLoadMore) {
                        if (postsWithVotes.isEmpty()) {
                            canLoadMore = false
                        } else {
                            allPosts.addAll(newPosts)
                            if (isLoadMore) {
                                currentPage++
                            }
                        }
                        _uiState.value = UiStatePost.SuccessWithVotes(postsWithVotes)
                    } else {
                        allPosts.addAll(newPosts)
                        _uiState.value = UiStatePost.SuccessWithVotes(postsWithVotes)
                        if (newPosts.isNotEmpty()) {
                            currentPage = 2
                        }
                    }

                    logDebug("Successfully fetched ${newPosts.size} posts and votes. Total: ${allPosts.size}")
                } else {
                    if (isLoadMore) {
                        canLoadMore = false
                        logError("Load more failed: ${response.message()}")
                    } else {
                        _uiState.value = UiStatePost.Error("Lỗi: ${response.message()}")
                        logError("Get post failed: ${response.message()}")
                    }
                }
            } catch (e: IOException) {
                if (isLoadMore) {
                    canLoadMore = false
                    logError("Load more - Server unreachable: ${e.message}")
                } else {
                    _uiState.value = UiStatePost.Error("Không thể kết nối server.")
                    logError("Server unreachable: ${e.message}")
                }
            } catch (e: Exception) {
                if (isLoadMore) {
                    canLoadMore = false
                    logError("Load more exception: ${e.localizedMessage}")
                } else {
                    _uiState.value = UiStatePost.Error("Lỗi không xác định: ${e.message}")
                    logError("Exception: ${e.localizedMessage}")
                }
            } finally {
                if (isRefresh) {
                    _isRefreshing.value = false
                }
                if (isLoadMore) {
                    _isLoadingMore.value = false
                }
            }
        }
    }
    fun createPost(createPostRequest: CreatePostRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.userService.createPost(createPostRequest)
                if (response.isSuccessful) {
                    _uiStateCreate.value = UiState.Success(
                        response.body()?.message ?: "Đăng bài thành công"
                    )
                    delay(2000)
                    _uiStateCreate.value = UiState.Idle
                } else {
                    showError("Đăng ký thất bại: ${response.message()}")
                    _uiStateCreate.value = UiState.Error(response.message())
                }
            } catch (e: IOException) {
                _uiStateCreate.value = UiState.Error("Lỗi phản hồi từ server")
                showError("Không thể kết nối máy chủ, vui lòng kiểm tra mạng.")
            } catch (e: Exception) {
                _uiStateCreate.value = UiState.Error("Lỗi không xác định: ${e.message}")
                showError("Lỗi không xác định: ${e.localizedMessage ?: "Không rõ"}")
            }
        }
    }


    private fun showError(message: String) {
        // TODO: Hiển thị lỗi lên UI, ví dụ Toast, Snackbar, hoặc cập nhật LiveData
        Log.e("Register", message)
    }
    fun loadMorePosts() {
        if (canLoadMore && !_isLoadingMore.value) {
            fetchPosts(GetPostRequest(page = currentPage), isLoadMore = true)
        }
    }

    fun refreshPosts() {
        fetchPosts(GetPostRequest(page = 1), isRefresh = true)
    }

    fun getStoredUserId(): String? {
        return sharedPreferences.getString("userId", null)
    }

    private fun logError(msg: String) {
        Log.e("PostViewModel", msg)
    }

    private fun logDebug(msg: String) {
        Log.d("PostViewModel", msg)
    }
}
package com.example.itforum.user.post.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.effect.model.UiStatePost
import com.example.itforum.user.model.request.CreatePostRequest
import com.example.itforum.user.model.request.GetPostRequest
import com.example.itforum.user.model.request.VoteRequest
import com.example.itforum.user.model.response.GetVoteResponse
import com.example.itforum.user.model.response.PostWithVote
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.IOException
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.http.Part
import java.io.File


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

    private val allPostsWithVotes = mutableListOf<PostWithVote>()
    private var userId = sharedPreferences.getString("userId", null)
    init {
        userId = sharedPreferences.getString("userId", null)
    }


    private suspend fun getVoteDataByPostId(postId: String?, userId: String?): GetVoteResponse? {
        if (postId.isNullOrEmpty() || userId.isNullOrEmpty()) return null
        return try {
            val response = RetrofitInstance.postService.getVoteData(postId, userId)
            Log.d("vote data", response.body().toString())
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.d("Error", "Vote fetch error: ${e.message}")
            null
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun fetchPosts(getPostRequest: GetPostRequest, isRefresh: Boolean = false, isLoadMore: Boolean = false) {
        if (isRefresh) {

            allPostsWithVotes.clear()
            _isRefreshing.value = true
            currentPage = 1
            canLoadMore = true
        } else if (isLoadMore) {
            if (!canLoadMore || _isLoadingMore.value) return
            _isLoadingMore.value = true
        } else {

            allPostsWithVotes.clear()
            _uiState.value = UiStatePost.Loading
            currentPage = 1
            canLoadMore = true
            Log.d("load state", canLoadMore.toString())
        }

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.postService.getPost(getPostRequest)
                if (response.isSuccessful && response.body() != null) {
                    val newPosts = response.body()?.posts ?: emptyList()
                    Log.d("post", newPosts.toString())
                    val postsWithVotes = newPosts.map { post ->
                        async {
                            PostWithVote(
                                post = post,
                                vote = getVoteDataByPostId(post.id, userId)
                            )
                        }
                    }.awaitAll()

                    // Pagination logic
                    if (newPosts.size < 3) {
                        canLoadMore = false
                    }

                    if (isRefresh || !isLoadMore) {
                        // Fresh load or refresh: both lists cleared above

                        allPostsWithVotes.addAll(postsWithVotes)
                        if (newPosts.isNotEmpty()) currentPage = 2
                    } else if (isLoadMore) {
                        // Load more: append new data

                        allPostsWithVotes.addAll(postsWithVotes)
                        if (newPosts.isNotEmpty()) currentPage++
                    }

                    // Always update the UI with all accumulated posts
                    _uiState.value = UiStatePost.SuccessWithVotes(allPostsWithVotes)
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

    fun createPost(createPostRequest: CreatePostRequest, context: Context) {
        viewModelScope.launch {
            _uiStateCreate.value = UiState.Loading
            try {
                Log.d("UserViewModel", "Request: $createPostRequest")

                val imageUrls = createPostRequest.imageUrls?.mapNotNull { uri ->
                    prepareFilePart(context, uri, "imageUrls")
                }

                // Chỉ tạo MultipartBody.Part cho các trường không null
                val userId = createPostRequest.userId?.let {
                    MultipartBody.Part.createFormData("userId", it)
                }
                val title = createPostRequest.title?.let {
                    MultipartBody.Part.createFormData("title", it)
                }
                val content = createPostRequest.content?.let {
                    MultipartBody.Part.createFormData("content", it)
                }
                val isPublished = createPostRequest.isPublished?.let {
                    MultipartBody.Part.createFormData("isPublished", it)
                }
                val tags = createPostRequest.tags?.let {
                    MultipartBody.Part.createFormData("tags", Gson().toJson(it))
                }

                val response = imageUrls?.let {
                    RetrofitInstance.postService.createPost(
                        userId = userId,
                        title = title,
                        content = content,
                        tags = tags,
                        isPublished = isPublished,
                        imageUrls = it
                    )
                }


                if (response != null) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("PostViewModel", "Success Response: ${responseBody?.message}")
                        _uiStateCreate.value = UiState.Success(
                            responseBody?.message ?: "Đăng bài thành công"
                        )
                        delay(500) // Cho Compose thời gian phản ứng trước khi đổi trạng thái
                        _uiStateCreate.value = UiState.Idle
                    } else {
                        // Get error details from response body
                        val errorBody = response.errorBody()?.string()
                        Log.e("PostViewModel", "Error Response Body: $errorBody")

                        val errorMessage = when (response.code()) {
                            400 -> "Dữ liệu không hợp lệ: $errorBody"
                            404 -> "Không tìm thấy người dùng"
                            500 -> "Lỗi server: $errorBody"
                            else -> "Lỗi không xác định (${response.code()}): $errorBody"
                        }

                        showError(errorMessage)
                        _uiStateCreate.value = UiState.Error(errorMessage)
                    }
                }
            } catch (e: IOException) {
                val errorMsg = "Lỗi kết nối mạng: ${e.localizedMessage}"
                Log.e("PostViewModel", errorMsg, e)
                _uiStateCreate.value = UiState.Error(errorMsg)
                showError("Không thể kết nối máy chú, vui lòng kiểm tra mạng.")
            } catch (e: Exception) {
                val errorMsg = "Lỗi hệ thống: ${e.message ?: e.localizedMessage}"
                Log.e("PostViewModel", errorMsg, e)
                _uiStateCreate.value = UiState.Error(errorMsg)
                showError("Lỗi bất ngờ: ${e.localizedMessage ?: "Không rõ"}")
            }
        }
    }
    private fun prepareFilePart(
        context: Context,
        fileUri: Uri,
        partName: String
    ): MultipartBody.Part? {
        return try {
            val inputStream = context.contentResolver.openInputStream(fileUri)
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, tempFile.name, requestFile)
        } catch (e: Exception) {
            Log.e("PostViewModel", "Error preparing file part", e)
            null
        }
    }

    fun loadMorePosts() {
        if (canLoadMore && !_isLoadingMore.value) {
            fetchPosts(GetPostRequest(page = currentPage), isLoadMore = true)
        }
    }
    fun votePost(postId: String?, type: String) {
        if (postId.isNullOrEmpty() || userId.isNullOrEmpty() || type.isEmpty()) return
        viewModelScope.launch {
            try {
                val voteRequest = VoteRequest(userId = userId, type = type)
                val response = RetrofitInstance.postService.votePost(postId, voteRequest)
                if (response.isSuccessful) {
                   //
                }
                // No return needed
            } catch (e: Exception) {
                // Handle error (optional: log or update error state)
            }
        }
    }

    fun refreshPosts() {
        fetchPosts(GetPostRequest(page = 1), isRefresh = true)
    }


    fun getStoredUserId(): String? {
        return sharedPreferences.getString("userId", null)
    }

    private fun showError(message: String) {
        Log.e("Register", message)
    }

    private fun logError(msg: String) {
        Log.e("PostViewModel", msg)
    }

    private fun logDebug(msg: String) {
        Log.d("PostViewModel", msg)
    }
    fun fetchComment(postId: String?){

    }
}
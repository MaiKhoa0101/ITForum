package com.example.itforum.user.post.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.effect.model.UiStatePost
import com.example.itforum.user.modelData.request.CreatePostRequest
import com.example.itforum.user.modelData.request.GetPostRequest
import com.example.itforum.user.modelData.request.VoteRequest
import com.example.itforum.user.modelData.response.BookMarkResponse
import com.example.itforum.user.modelData.response.GetBookMarkResponse
import com.example.itforum.user.modelData.response.GetVoteResponse
import com.example.itforum.user.modelData.response.PostResponse
import com.example.itforum.user.modelData.response.PostWithVote
import com.example.itforum.user.modelData.response.UserProfileResponse
import com.example.itforum.user.modelData.response.VoteResponse
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

    private val _postsWithVotes = MutableStateFlow<List<PostWithVote>>(emptyList())
    val postsWithVotes: StateFlow<List<PostWithVote>> = _postsWithVotes


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
            currentPage = 1
            canLoadMore = true
            Log.d("load state", canLoadMore.toString())
        }

        viewModelScope.launch {
            try {
                // fetch bookmarked post IDs
                val bookmarkResponse = getSavePost(userId)
                val bookmarkedIds = bookmarkResponse?.postsId?.toSet() ?: emptySet()
                Log.d("bookmarkId",bookmarkedIds.toString())
                // fetch post
                val response = RetrofitInstance.postService.getPost(getPostRequest)
                if (response.isSuccessful && response.body() != null) {
                    val newPosts = response.body()?.posts ?: emptyList()
                    Log.d("post", newPosts.toString())
                    val postsWithVotes = newPosts.map { post ->
                        async {
                            PostWithVote(
                                post = post,
                                vote = getVoteDataByPostId(post.id, userId),
                                isBookMark = bookmarkedIds.contains(post.id)

                            )
                        }
                    }.awaitAll()

                    // Pagination logic
                    if (newPosts.size < 3) {
                        canLoadMore = false
                    }

                    if (isRefresh || !isLoadMore) {
                        allPostsWithVotes.addAll(postsWithVotes)
                        if (newPosts.isNotEmpty()) currentPage = 2
                    } else if (isLoadMore) {
                        allPostsWithVotes.addAll(postsWithVotes)
                        if (newPosts.isNotEmpty()) currentPage++
                    }

                    // Always update posts flow
                    _postsWithVotes.value = allPostsWithVotes
                } else {
                    if (isLoadMore) {
                        canLoadMore = false
                        logError("Load more failed: ${response.message()}")
                    } else {
                        // Optionally emit error to another error flow/UI state
                        logError("Get post failed: ${response.message()}")
                    }
                }
            } catch (e: IOException) {
                if (isLoadMore) {
                    canLoadMore = false
                    logError("Load more - Server unreachable: ${e.message}")
                } else {
                    // Optionally emit error to another error flow/UI state
                    logError("Server unreachable: ${e.message}")
                }
            } catch (e: Exception) {
                if (isLoadMore) {
                    canLoadMore = false
                    logError("Load more exception: ${e.localizedMessage}")
                } else {
                    // Optionally emit error to another error flow/UI state
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

                val videoUrls = createPostRequest.videoUrls?.mapNotNull { uri ->
                    prepareFilePart(context, uri, "videoUrls")
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

                val response =
                    videoUrls?.let {
                        imageUrls?.let { it1 ->
                            RetrofitInstance.postService.createPost(
                                userId = userId,
                                title = title,
                                content = content,
                                tags = tags,
                                isPublished = isPublished,
                                imageUrls = it1,
                                videoUrls = it
                            )
                        }
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
            val mimeType = context.contentResolver.getType(fileUri) ?: "application/octet-stream"
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "tmp"
            val tempFile = File.createTempFile("upload_", ".$extension", context.cacheDir)

            tempFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, tempFile.name, requestFile)
        } catch (e: Exception) {
            Log.e("Upload", "Error preparing file part", e)
            null
        }
    }


    fun loadMorePosts() {
        if (canLoadMore && !_isLoadingMore.value) {
            fetchPosts(GetPostRequest(page = currentPage), isLoadMore = true)
        }
    }
    suspend fun votePost(postId: String?, type: String, index: Int): VoteResponse? {
        if (postId.isNullOrEmpty() || userId.isNullOrEmpty() || type.isEmpty()) return null
        Log.d("Index of post", index.toString())

        return try {
            val voteRequest = VoteRequest(userId = userId, type = type)
            val response = RetrofitInstance.postService.votePost(postId, voteRequest)
            if (response.isSuccessful) {
                response.body() // Return the VoteResponse
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("VotePost", "Error voting: ${e.message}")
            null
        }
    }
    suspend fun savePost(postId: String?, userId: String?): BookMarkResponse? {
        return try {
            if (postId.isNullOrEmpty() || userId.isNullOrEmpty()) {
                Log.e("savePost", "postId or userId is null/empty")
                return null
            }

            val res = RetrofitInstance.postService.savedPost(postId, userId)


            if (res.isSuccessful) {
                val response = res.body()
                Log.d("savePost", response.toString())
                response
            } else {
                Log.e("savePost", "Failed with code: ${res.code()}, message: ${res.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("savePost", "Error: ${e.message}")
            null
        }
    }

    suspend fun getSavePost(userId: String?): GetBookMarkResponse? {
        return try {
            if (userId.isNullOrEmpty()) {
                Log.e("getSavePost", "userId is null or empty")
                return null
            }

            val res = RetrofitInstance.postService.getSavedPost(userId)

            if (res.isSuccessful) {
                val response = res.body()
                Log.d("getSavePost", response.toString())
                response
            } else {
                Log.e("getSavePost", "Failed with code: ${res.code()}, message: ${res.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("getSavePost", "Exception: ${e.message}")
            null
        }
    }


    fun refreshPosts(getPostRequest: GetPostRequest) {
        fetchPosts(getPostRequest, isRefresh = true)
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
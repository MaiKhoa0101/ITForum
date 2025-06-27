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
import com.example.itforum.repository.PostRepository
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.service.toEntity
import com.example.itforum.service.toModel
import com.example.itforum.user.ProgressRequestBody.ProgressRequestBody
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
import com.example.itforum.user.modelData.response.Vote
import com.example.itforum.user.modelData.response.VoteResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger


class PostViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiStatePost>(UiStatePost.Loading)
    val uiState: StateFlow<UiStatePost> = _uiState.asStateFlow()

    private val _uiStateCreate = MutableStateFlow<UiState>(UiState.Idle)
    val uiStateCreate: StateFlow<UiState> = _uiStateCreate.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _selectedPost = MutableStateFlow<PostResponse?>(null)
    val selectedPost: StateFlow<PostResponse?> = _selectedPost.asStateFlow()

    private val _selectedVote = MutableStateFlow<GetVoteResponse?>(null)
    val selectedVote: StateFlow<GetVoteResponse?> = _selectedVote.asStateFlow()

    private var currentPage = 1
    private var canLoadMore = true

    private val allPostsWithVotes = mutableListOf<PostWithVote>()
    private var userId = sharedPreferences.getString("userId", null)

    private val _postsWithVotes = MutableStateFlow<List<PostWithVote>>(emptyList())
    val postsWithVotes: StateFlow<List<PostWithVote>> = _postsWithVotes

    private val _listPost = MutableStateFlow<List<PostResponse>>(emptyList())
    val listPost: StateFlow<List<PostResponse>> = _listPost

    private val _post = MutableStateFlow<PostResponse?>(null)
    val post: StateFlow<PostResponse?> = _post

    private val _listVote = MutableStateFlow<List<Vote>>(emptyList())
    val listVote: StateFlow<List<Vote>> = _listVote
    private val postRepository = PostRepository()

    fun getAllPost() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.postService.getAllPost()
                if (response.isSuccessful) {
                    _listPost.value = response.body()?.listPost ?: emptyList()
                }
                else {
                    showError("Response get không hợp lệ")            }
            }
            catch (e: IOException) {
                showError("Không thể kết nối máy chủ, vui lòng kiểm tra mạng.")
            }
            catch (e: Exception) {
                showError("Lỗi mạng hoặc bất ngờ: ${e.localizedMessage ?: "Không rõ"}")
            }
        }
    }


    fun fetchPostById(postId: String?) {
        viewModelScope.launch {
            try {
                val res = RetrofitInstance.postService.getPostById(postId.toString())
                if (res.isSuccessful) {
                    if (res.isSuccessful) {
                        _selectedPost.value = res.body()?.post
                        println("fetch post by id" + _selectedPost.value.toString())
                    }
                } else {
                    Log.e("fetch post by id", "Error: ${res.code()} - ${res.errorBody()?.string()}")
                }
            } catch (e: IOException) {
                Log.e("fetch post by id", "IOException: ${e.message}")
            } catch (e: Exception) {
                Log.e("fetch post by id", "Unexpected error: ${e.message}")
            }
        }
    }

<<<<<<< HEAD

=======
>>>>>>> 972fb5e8fb7caba83038d0db2c5c7292b969e3a5

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
            _isLoading.value = true
            Log.d("load state", canLoadMore.toString())
        }

        viewModelScope.launch {
            try {
                // fetch bookmarked post IDs
                val bookmarkResponse = postRepository.getSavePost(userId)
                val bookmarkedIds = bookmarkResponse?.postsId?.toSet() ?: emptySet()

                // fetch post
                val response = RetrofitInstance.postService.getPost(getPostRequest)
                if (response.isSuccessful && response.body() != null) {
                    val newPosts = response.body()?.posts ?: emptyList()

                    val postsWithVotes = newPosts.map { post ->
                        async {
                            PostWithVote(
                                post = post,
                                vote = postRepository.getVoteDataByPostId(post.id, userId),
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
                if (!isRefresh && !isLoadMore) {
                    _isLoading.value = false
                }
            }
        }
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
    fun createPost(createPostRequest: CreatePostRequest, context: Context) {
        viewModelScope.launch {
            _uiStateCreate.value = UiState.Loading
            _uploadProgress.value = 0f
            try {
                Log.d("PostViewModel", "Request: $createPostRequest")
                var uploadedFilesRef = AtomicInteger(0)
                val totalFiles = (createPostRequest.imageUrls?.size ?: 0) + (createPostRequest.videoUrls?.size ?: 0)

                val imageUrls: List<MultipartBody.Part>
                val videoUrls: List<MultipartBody.Part>
                withContext(Dispatchers.IO) {
                    imageUrls = prepareAndUploadFiles(
                        context,
                        createPostRequest.imageUrls,
                        "imageUrls",
                        totalFiles,
                        uploadedFilesRef
                    )

                    videoUrls = prepareAndUploadFiles(
                        context,
                        createPostRequest.videoUrls,
                        "videoUrls",
                        totalFiles,
                        uploadedFilesRef
                    )
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
                val tags = createPostRequest.tags?.mapNotNull  {
                    it?.let { it1 -> MultipartBody.Part.createFormData("tags", it1) }
                }
                Log.d("PostViewModel tags", "tags: "+ tags)

                val response =
                    videoUrls.let {
                        imageUrls.let { it1 ->
                            tags?.let { it2 ->
                                RetrofitInstance.postService.createPost(
                                    userId = userId,
                                    title = title,
                                    content = content,
                                    tags = it2,
                                    isPublished = isPublished,
                                    imageUrls = it1,
                                    videoUrls = it
                                )
                            }
                        }
                    }


                if (response != null) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("PostViewModel", "Success Response: ${responseBody?.message}")
                        _uiStateCreate.value = UiState.Success(
                            responseBody?.message ?: "Đăng bài thành công"
                        )
                        _uploadProgress.value = 0f
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
    private suspend fun prepareAndUploadFiles(
        context: Context,
        uris: List<Uri>?,
        partName: String,
        totalFiles: Int,
        uploadedFilesRef: AtomicInteger
    ): List<MultipartBody.Part> {
        val parts = mutableListOf<MultipartBody.Part>()
        if (uris.isNullOrEmpty()) return parts
        for (uri in uris) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
                val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "tmp"
                val tempFile = withContext(Dispatchers.IO) {
                    File.createTempFile("upload_", ".$extension", context.cacheDir)
                }

                tempFile.outputStream().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
                val progressBody = ProgressRequestBody(tempFile, mimeType) { bytesWritten, contentLength ->
                    val percent = bytesWritten.toFloat() / contentLength.toFloat()
                    val overallProgress = (uploadedFilesRef.get() + percent) / totalFiles
                    this._uploadProgress.value = overallProgress
                    if(bytesWritten.toFloat() == contentLength.toFloat()) uploadedFilesRef.incrementAndGet()
                }

                val part = MultipartBody.Part.createFormData(partName, tempFile.name, progressBody)
                parts.add(part)
            } catch (e: Exception) {
                Log.e("Upload", "Error preparing file part for $uri", e)
                // Optional: continue to next URI
            }
        }

        return parts
    }
    fun getPostById(id: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.postService.getPostById(id)
                Log.d("DETAIL", "Code: ${response.code()}, Body: ${response.body()}")
                if (response.isSuccessful) {
                    _post.value = response.body()?.post
                } else {
                    showError("Response get không hợp lệ, ${response.code()}")
                Log.e("Post by id", "Lỗi response không thành công: ${response.message()}")
                }
            }
            catch (e: IOException) {
                showError("Không thể kết nối máy chủ, vui lòng kiểm tra mạng.")
            }
            catch (e: Exception) {
                showError("Lỗi mạng hoặc bất ngờ: ${e.localizedMessage ?: "Không rõ"}")
            }
        }
    }
    fun loadMorePosts(getPostRequest: GetPostRequest) {
        if (canLoadMore && !_isLoadingMore.value) {
            val updatedRequest = getPostRequest.copy(page = currentPage)
            fetchPosts(updatedRequest, isLoadMore = true)
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
        Log.d("check data when refresh",getPostRequest.toString())
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
    fun setSelectedPost(post: PostResponse, vote: GetVoteResponse?) {
        _selectedPost.value = post
        _selectedVote.value = vote
    }

    fun clearSelectedPost() {
        _selectedPost.value = null
        _selectedVote.value = null
    }
    fun handleUpVote(type: String, index: Int, postId: String?) {
        viewModelScope.launch {
            try {
                val res = votePost(postId, type, index)
                if (res != null) {
                    val currentList = _postsWithVotes.value.toMutableList()
                    val currentItem = currentList[index]

                    val updatedVote = currentItem.vote?.copy(
                        data = currentItem.vote.data?.copy(
                            upVoteData = currentItem.vote.data!!.upVoteData?.copy(
                                total = res.data?.upvotes ?: currentItem.vote.data!!.upVoteData!!.total
                            ),
                            userVote = res.data?.userVote
                        )
                    )

                    val updatedItem = currentItem.copy(vote = updatedVote)
                    currentList[index] = updatedItem
                    _postsWithVotes.value = currentList
                }
            } catch (e: Exception) {
                Log.e("handleUpVote", "Error", e)
            }
        }
    }
    fun handleDownVote(type: String, index: Int, postId: String?) {
        viewModelScope.launch {
            try {
                val res = votePost(postId, type, index)
                if (res != null) {
                    val currentList = _postsWithVotes.value.toMutableList()
                    val currentItem = currentList[index]

                    val updatedVote = currentItem.vote?.copy(
                        data = currentItem.vote.data?.copy(
                            upVoteData = currentItem.vote.data!!.upVoteData?.copy(
                                total = res.data?.upvotes ?: currentItem.vote.data!!.upVoteData!!.total
                            ),
                            userVote = res.data?.userVote
                        )
                    )

                    val updatedItem = currentItem.copy(vote = updatedVote)
                    currentList[index] = updatedItem

                    _postsWithVotes.value = currentList
                } else {
                    Log.d("Downvote", "Failed to vote")
                }
            } catch (e: Exception) {
                Log.e("handleDownVote", "Exception", e)
            }
        }
    }

    fun handleBookmark(index: Int, postId: String?, userId: String?) {
        viewModelScope.launch {
            try {
                val res = savePost(postId, userId)
                val currentList = _postsWithVotes.value.toMutableList()
                val currentItem = currentList[index]

                val updatedItem = currentItem.copy(
                    isBookMark = !currentItem.isBookMark
                )

                currentList[index] = updatedItem
                _postsWithVotes.value = currentList

                Log.d("Bookmark", "Bookmark toggled for post: $postId")
            } catch (e: Exception) {
                Log.e("handleBookmark", "Bookmark error", e)
            }
        }
    }




}
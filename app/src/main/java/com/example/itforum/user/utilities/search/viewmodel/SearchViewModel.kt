package com.example.itforum.user.utilities.search.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.repository.PostRepository
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.modelData.request.GetPostRequest
import com.example.itforum.user.modelData.response.PostWithVote
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    sharedPreferences: SharedPreferences
) : ViewModel() {
    private var userId = sharedPreferences.getString("userId", null)
    private val postRepository = PostRepository()


    private val _postsByTitle = MutableStateFlow<List<PostWithVote>>(emptyList())
    val postsByTitle: StateFlow<List<PostWithVote>> = _postsByTitle

    private val _postsByTags = MutableStateFlow<List<PostWithVote>>(emptyList())
    val postsByTags: StateFlow<List<PostWithVote>> = _postsByTags


    private suspend fun searchPost(getPostRequest: GetPostRequest): List<PostWithVote> =
        coroutineScope {
            try {

                val bookmarkResponse = postRepository.getSavePost(userId)
                val bookmarkedIds = bookmarkResponse?.postsId?.toSet() ?: emptySet()
                val res = RetrofitInstance.postService.getPost(getPostRequest)
                if (res.isSuccessful && res.body() != null) {
                    val newPosts = res.body()?.posts ?: emptyList()
                    Log.d("searchingg", res.body().toString())
                    newPosts.map { post ->
                        async {
                            PostWithVote(
                                post = post,
                                vote = postRepository.getVoteDataByPostId(post.id, userId),
                                isBookMark = bookmarkedIds.contains(post.id)
                            )
                        }
                    }.awaitAll()

                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }

    fun resultByTitle(title: String) {
        viewModelScope.launch {
            val result = searchPost(GetPostRequest(title = title, page = 1))
            _postsByTitle.value = result
            Log.d("search title", result.toString())
        }
    }

    fun resultByTags(tag: String) {
        viewModelScope.launch {
            val result = searchPost(GetPostRequest(tags = listOf(tag), page = 1))
            _postsByTags.value = result
        }
    }
}
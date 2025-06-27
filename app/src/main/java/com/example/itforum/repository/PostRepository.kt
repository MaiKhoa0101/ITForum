package com.example.itforum.repository

import android.util.Log
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.modelData.response.BookMarkResponse
import com.example.itforum.user.modelData.response.GetBookMarkResponse
import com.example.itforum.user.modelData.response.GetVoteResponse

class PostRepository {
    suspend fun getVoteDataByPostId(postId: String?, userId: String?): GetVoteResponse? {
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
}
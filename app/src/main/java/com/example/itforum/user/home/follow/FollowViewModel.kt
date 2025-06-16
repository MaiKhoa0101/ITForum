package com.example.itforum.user.home.follow

import GetFollowerResponse
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.modelData.response.PostFollowResponse
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FollowViewModel(
    private val navHostController: NavHostController,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    // Mutable state for follower data
    private val _followerData = mutableStateOf<GetFollowerResponse?>(null)
    val followerData: State<GetFollowerResponse?> = _followerData

    // Mutable state for random users
    private val _randomUsers = mutableStateOf<GetFollowerResponse?>(null)
    val randomUsers: State<GetFollowerResponse?> = _randomUsers

    // Loading states
    private val _isLoadingFollowers = mutableStateOf(false)
    val isLoadingFollowers: State<Boolean> = _isLoadingFollowers

    private val _isLoadingRandomUsers = mutableStateOf(false)
    val isLoadingRandomUsers: State<Boolean> = _isLoadingRandomUsers

    // Error states
    private val _followersError = mutableStateOf<String?>(null)
    val followersError: State<String?> = _followersError

    private val _randomUsersError = mutableStateOf<String?>(null)
    val randomUsersError: State<String?> = _randomUsersError

    suspend fun followUser(userId: String, followerId: String): PostFollowResponse? {
        return try {
            val res = RetrofitInstance.followService.follow(userId, followerId)
            if (res.isSuccessful) {
                Log.d("follow", res.body().toString())
                res.body()
            } else {
                Log.e("follow", "Request failed: ${res.code()} - ${res.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("follow", "Exception occurred: ${e.message}", e)
            null
        }
    }

    fun loadFollowerData(userId: String) {
        viewModelScope.launch {
            _isLoadingFollowers.value = true
            _followersError.value = null

            try {
                val result = getFollowerData(userId)
                _followerData.value = result
                if (result == null) {
                    _followersError.value = "Failed to load follower data"
                }
            } catch (e: Exception) {
                _followersError.value = "Error: ${e.message}"
            } finally {
                _isLoadingFollowers.value = false
            }
        }
    }

    fun loadRandomUsers(userId: String, size: String) {
        viewModelScope.launch {
            _isLoadingRandomUsers.value = true
            _randomUsersError.value = null

            try {
                val result = getRandomUser(userId, size)
                _randomUsers.value = result
                if (result == null) {
                    _randomUsersError.value = "Failed to load random users"
                }
            } catch (e: Exception) {
                _randomUsersError.value = "Error: ${e.message}"
            } finally {
                _isLoadingRandomUsers.value = false
            }
        }
    }

    private suspend fun getFollowerData(userId: String): GetFollowerResponse? {
        return try {
            val res = RetrofitInstance.followService.getFollowData(userId)
            if (res.isSuccessful) {
                Log.d("Follow Data", res.body().toString())
                res.body()
            } else {
                Log.e("followData", "Request failed: ${res.code()} - ${res.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("followData", "Exception occurred: ${e.message}", e)
            null
        }
    }

    private suspend fun getRandomUser(userId: String, size: String): GetFollowerResponse? {
        return try {
            val res = RetrofitInstance.followService.getRandomUser(userId, size)
            if (res.isSuccessful) {
                Log.d("Random user Data", res.body().toString())
                res.body()
            } else {
                Log.e("Random user Data", "Request failed: ${res.code()} - ${res.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("Random user Data", "Exception occurred: ${e.message}", e)
            null
        }
    }
}
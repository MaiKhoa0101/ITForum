package com.example.itforum.user.modelData.response

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("_id") val id: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val introduce: String = "",
    val avatar: String = "",
    val totalPost: Int = 0,
    val numberComment: Int = 0,
    val isBanned: Boolean = false,
    val certificate: List<Certificate> = emptyList(),
    val skill: List<String> = emptyList(),
    val createdAt: String = "",
    val bannedUntil: String? = null
)


data class fetchUserState(
    val message:String,
    @SerializedName("user") val userProfileResponse: UserProfileResponse
)

data class userUpdateResponse(
    val message: String
)

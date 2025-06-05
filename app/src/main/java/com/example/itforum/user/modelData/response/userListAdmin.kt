package com.example.itforum.user.model.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("_id") val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val role: String,
    val username:String,
    val introduce: String,
    val avatar: String,
    val numberPost: Int,
    val numberComment: Int,
    val isBanned: Boolean,
    val certificate: List<Certificate>,
    val skill: List<Skill>
)

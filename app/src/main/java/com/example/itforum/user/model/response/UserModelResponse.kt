package com.example.itforum.user.model.response

import com.google.gson.annotations.SerializedName

data class UserModelResponse(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val password: String
)

data class SignUpResponse(
    val message: String
)

data class LoginResponse(
    val accessToken: String
)


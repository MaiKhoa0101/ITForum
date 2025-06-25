package com.example.itforum.user.modelData.request
data class RegisterUser(
    val name: String,
    val phone: String?,
    val email: String,
    val password: String,
)

data class LoginUser(
    val emailOrPhone: String,
    val password: String,
)
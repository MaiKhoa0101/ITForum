package com.example.itforum.user.login.otp

import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/send-otp")
    suspend fun sendOtp(@Body body: Map<String, String>): Response<String>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body body: Map<String, String>): Response<String>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body body: Map<String, String>): Response<String>
}


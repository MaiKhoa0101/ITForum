package com.example.itforum.service

import com.example.itforum.user.model.request.LoginUser
import com.example.itforum.user.model.request.RegisterUser
import com.example.itforum.user.model.response.LoginResponse
import com.example.itforum.user.model.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {

    @POST("auth/register")
    suspend fun register(@Body registerUser: RegisterUser): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body loginUser: LoginUser): Response<LoginResponse>


}
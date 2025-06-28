package com.example.itforum.service

import com.example.itforum.user.modelData.request.LoginUser
import com.example.itforum.user.modelData.request.RegisterUser
import com.example.itforum.user.modelData.response.LoginResponse
import com.example.itforum.user.modelData.response.RegisterResponse
import com.example.itforum.user.modelData.response.SignOutResponse
import com.example.itforum.user.modelData.response.UserResponse
import com.example.itforum.user.modelData.response.fetchUserState
import com.example.itforum.user.modelData.response.userUpdateResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface UserService {

    @POST("auth/register")
    suspend fun register(@Body registerUser: RegisterUser): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body loginUser: LoginUser): Response<LoginResponse>

    @Multipart
    @PUT("user/update/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Part name: MultipartBody.Part?,
        @Part phone: MultipartBody.Part?,
        @Part email: MultipartBody.Part?,
        @Part username: MultipartBody.Part?,
        @Part introduce: MultipartBody.Part?,
        @Part skill: List<MultipartBody.Part?>,
        @Part certificate: MultipartBody.Part?,
        @Part avatar: MultipartBody.Part?
    ): Response<userUpdateResponse>


    @GET("user/get/{id}")
    suspend fun getUser(@Path("id")id:String?): Response<fetchUserState>

    @GET("user/getall")
    suspend fun getAllUser(): Response<List<UserResponse>>

    @PUT("user/signOut/{userId}/{fcmToken}")
    suspend fun signOut(@Path("userId") userId:String, @Path("fcmToken") fcmToken:String): Response<SignOutResponse>
}
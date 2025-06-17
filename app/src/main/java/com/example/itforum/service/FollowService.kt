package com.example.itforum.service

import GetFollowerResponse
import com.example.itforum.user.modelData.response.BookMarkResponse
import com.example.itforum.user.modelData.response.GetBookMarkResponse
import com.example.itforum.user.modelData.response.PostFollowResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FollowService {
    @POST("follow/{userId}/{followerId}")
    suspend fun follow(@Path("userId")userId: String,@Path("followerId")followerId: String) : Response<PostFollowResponse>
    @GET("follow/{userId}")
    suspend fun getFollowData(@Path("userId")userId: String): Response<GetFollowerResponse>
    @GET("follow/random/{userId}/{size}")
    suspend fun getRandomUser(@Path("userId")userId: String , @Path("size")size : String): Response<GetFollowerResponse>
}
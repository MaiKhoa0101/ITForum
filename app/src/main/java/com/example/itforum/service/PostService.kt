package com.example.itforum.service;



import com.example.itforum.user.model.request.GetPostRequest
import com.example.itforum.user.model.request.VoteRequest
import com.example.itforum.user.model.response.GetVoteResponse
import com.example.itforum.user.model.response.PostListResponse

import com.example.itforum.user.model.response.VoteResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET

import retrofit2.http.POST

import retrofit2.http.Path
interface PostService {


    @POST("posts/search")
    suspend fun getPost(@Body getPostRequest : GetPostRequest) : Response<PostListResponse>

    @POST("vote/{postId}")
    suspend fun votePost(@Path("postId") postId: String, @Body voteRequest: VoteRequest): Response<VoteResponse>

    @GET("vote/{postId}/{userId}")
    suspend fun getVoteData(@Path("postId") postId: String, @Path("userId") userId: String): Response<GetVoteResponse>
}

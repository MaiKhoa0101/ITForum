package com.example.itforum.service;
import com.example.itforum.user.modelData.request.CreatePostRequest
import com.example.itforum.user.modelData.request.GetPostRequest
import com.example.itforum.user.modelData.request.PostComment
import com.example.itforum.user.modelData.request.PostReply
import com.example.itforum.user.modelData.request.VoteRequest
import com.example.itforum.user.modelData.response.CommentResponse
import com.example.itforum.user.modelData.response.CreatePostResponse
import com.example.itforum.user.modelData.response.GetVoteResponse
import com.example.itforum.user.modelData.response.PostCommentResponse
import com.example.itforum.user.modelData.response.PostListResponse
import com.example.itforum.user.modelData.response.PostReplyResponse
import com.example.itforum.user.modelData.response.ReplyResponse

import com.example.itforum.user.modelData.response.VoteResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET

import retrofit2.http.POST

import retrofit2.http.Path
import retrofit2.http.Query

interface PostService {


    @POST("posts/search")
    suspend fun getPost(@Body getPostRequest : GetPostRequest) : Response<PostListResponse>

    @POST("vote/{postId}")
    suspend fun votePost(@Path("postId") postId: String, @Body voteRequest: VoteRequest): Response<VoteResponse>

    @GET("vote/{postId}/{userId}")
    suspend fun getVoteData(@Path("postId") postId: String, @Path("userId") userId: String): Response<GetVoteResponse>
    @GET("comments/post/{postId}")
    suspend fun getCommentData(
        @Path("postId") postId: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<CommentResponse>
    @GET("comments/reply/{commentId}")
    suspend fun getRepliesData(
        @Path("commentId") postId: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<ReplyResponse>

    @POST("posts/create")
    suspend fun postComment(@Body postComment: PostComment): Response<PostCommentResponse>

    @POST("posts/create")
    suspend fun createPost(@Body createPostRequest: CreatePostRequest): Response<CreatePostResponse>

    @POST("posts/reply")
    suspend fun postReply(@Body postReply : PostReply): Response<PostReplyResponse>
}

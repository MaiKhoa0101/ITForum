package com.example.itforum.service;
import com.example.itforum.user.model.request.CreatePostRequest
import com.example.itforum.user.model.request.GetPostRequest
import com.example.itforum.user.model.request.PostComment
import com.example.itforum.user.model.request.PostReply
import com.example.itforum.user.model.request.VoteRequest
import com.example.itforum.user.model.response.CommentResponse
import com.example.itforum.user.model.response.CreatePostResponse
import com.example.itforum.user.model.response.GetVoteResponse
import com.example.itforum.user.model.response.PostCommentResponse
import com.example.itforum.user.model.response.PostListResponse
import com.example.itforum.user.model.response.PostReplyResponse
import com.example.itforum.user.model.response.ReplyResponse

import com.example.itforum.user.model.response.VoteResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

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
    @PATCH("posts/hide/{id}")
    suspend fun hidePost(@Path("id") postId: String): Response<Unit>
}

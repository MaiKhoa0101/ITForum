package com.example.itforum.service;
import android.net.Uri

import com.example.itforum.user.modelData.request.CreatePostRequest
import com.example.itforum.user.modelData.request.GetPostRequest
import com.example.itforum.user.modelData.request.PostComment
import com.example.itforum.user.modelData.request.PostReply
import com.example.itforum.user.modelData.request.VoteRequest
import com.example.itforum.user.modelData.response.BookMarkResponse
import com.example.itforum.user.modelData.response.CommentResponse
import com.example.itforum.user.modelData.response.CreatePostResponse
import com.example.itforum.user.modelData.response.GetBookMarkResponse
import com.example.itforum.user.modelData.response.GetVoteResponse
import com.example.itforum.user.modelData.response.Post
import com.example.itforum.user.modelData.response.PostCommentResponse
import com.example.itforum.user.modelData.response.PostListResponse
import com.example.itforum.user.modelData.response.PostListWrapper
import com.example.itforum.user.modelData.response.PostReplyResponse
import com.example.itforum.user.modelData.response.PostResponse
import com.example.itforum.user.modelData.response.PostWrapperResponse
import com.example.itforum.user.modelData.response.ReplyResponse
import com.example.itforum.user.modelData.response.Vote
import com.example.itforum.user.modelData.response.VoteListWrapper

import com.example.itforum.user.modelData.response.VoteResponse
import okhttp3.MultipartBody

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH

import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

import retrofit2.http.Path
import retrofit2.http.Query

interface PostService {

    @GET("posts/all")
    suspend fun getAllPost(): Response<PostListWrapper>

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

    @POST("comments")
    suspend fun postComment(@Body postComment: PostComment): Response<PostCommentResponse>

    @Multipart
    @POST("posts/create")
    suspend fun createPost(
        @Part userId: MultipartBody.Part?,
        @Part title: MultipartBody.Part?,
        @Part content: MultipartBody.Part?,
        @Part tags: List<MultipartBody.Part?>,
        @Part isPublished: MultipartBody.Part?,
        @Part imageUrls: List<MultipartBody.Part?>,
        @Part videoUrls: List<MultipartBody.Part?>
    ): Response<CreatePostResponse>

    @POST("comments/reply")
    suspend fun postReply(@Body postReply : PostReply): Response<PostReplyResponse>
    @PATCH("posts/hide/{id}")
    suspend fun hidePost(@Path("id") postId: String): Response<Unit>
    @POST("posts/bookmarks/{postId}/{userId}")
    suspend fun savedPost(@Path("postId")postId: String,@Path("userId")userId: String) : Response<BookMarkResponse>
    @GET("posts/bookmarks/{userId}")
    suspend fun getSavedPost(@Path("userId")userId: String): Response<GetBookMarkResponse>

    @GET("vote/all")
    suspend fun getAllVote(): Response<VoteListWrapper>
    @GET("/posts/single/{postId}")
    suspend fun getPostById(@Path("postId") postId: String): Response<Post>

    @Multipart
    @PUT("posts/{postId}")
    suspend fun updatePost(
        @Path("postId") postId: String,
        @Part title: MultipartBody.Part?,
        @Part content: MultipartBody.Part?,
        @Part tags: List<MultipartBody.Part?>,
        @Part isPublished: MultipartBody.Part?,
        @Part imageUrls: List<MultipartBody.Part?>,
        @Part videoUrls: List<MultipartBody.Part?>,
        @Part newImageUrls: List<MultipartBody.Part?>,
        @Part newVideoUrls: List<MultipartBody.Part?>
    ): Response<PostResponse>
}

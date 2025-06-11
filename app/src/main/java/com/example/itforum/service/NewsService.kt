package com.example.itforum.service

import com.example.itforum.user.modelData.request.NewsRequest
import com.example.itforum.user.modelData.response.CreateNewsResponse
import com.example.itforum.user.modelData.response.News
import com.example.itforum.user.modelData.response.NewsResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface NewsService {
    @GET("news/getall")
    suspend fun getNews(): Response<NewsResponse>

    @GET("news/get/{id}")
    suspend fun getNewsById(@Path("id") id: String): Response<News>

    @Multipart
    @POST("news/create")
    suspend fun createNews(
        @Part adminId: MultipartBody.Part?,
        @Part title: MultipartBody.Part?,
        @Part content: MultipartBody.Part?,
        @Part img: MultipartBody.Part?,
    ): Response<CreateNewsResponse>

    @PATCH("news/update/{id}")
    suspend fun updateNews(@Path("id") id: String, @Body news: NewsRequest): Response<News>

    @DELETE("news/delete/{id}")
    suspend fun deleteNews(@Path("id") id: String): Response<Void>

}
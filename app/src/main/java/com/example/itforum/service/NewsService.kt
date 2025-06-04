package com.example.itforum.service

import com.example.itforum.user.model.request.NewsRequest
import com.example.itforum.user.model.response.News
import com.example.itforum.user.model.response.NewsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface NewsService {
    @GET("news/getall")
    suspend fun getNews(): Response<NewsResponse>

    @GET("news/get/{id}")
    suspend fun getNewsById(@Path("id") id: String): Response<News>

    @POST("news/create")
    suspend fun createNews(@Body news: NewsRequest): Response<News>

    @PATCH("news/update/{id}")
    suspend fun updateNews(@Path("id") id: String, @Body news: NewsRequest): Response<News>

    @DELETE("news/delete/{id}")
    suspend fun deleteNews(@Path("id") id: String): Response<Void>

}
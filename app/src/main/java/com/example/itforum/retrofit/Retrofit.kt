package com.example.itforum.retrofit


import com.example.itforum.service.ComplaintService
import com.example.itforum.service.UserService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.getValue
import kotlin.jvm.java

import com.example.itforum.service.ReportPostService
import com.example.itforum.service.NewsService
import com.example.itforum.service.PostService
import com.example.itforum.service.ReportAccountService

object RetrofitInstance {



    private const val BASE_URL = "http://192.168.1.7:4000"

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient) // ← Đảm bảo dùng client có timeout
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val reportAccountService: ReportAccountService by lazy { retrofit.create(ReportAccountService::class.java) }
    val userService: UserService by lazy {retrofit.create(UserService::class.java) }
    val reportPostService: ReportPostService by lazy { retrofit.create(ReportPostService::class.java) }
    val newsService: NewsService by lazy { retrofit.create(NewsService::class.java) }
    val postService: PostService by lazy {retrofit.create(PostService::class.java) }
    val complaintService: ComplaintService by lazy {retrofit.create(ComplaintService::class.java) }
}
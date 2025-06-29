package com.example.itforum.retrofit


import com.example.itforum.service.AnalyticsApi
import com.example.itforum.admin.adminCrashlytic.AiCrashService
import com.example.itforum.service.ComplaintService
import com.example.itforum.service.FollowService
import com.example.itforum.service.UserService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.getValue
import kotlin.jvm.java

import com.example.itforum.service.ReportPostService
import com.example.itforum.service.NewsService
import com.example.itforum.service.NotificationService
import com.example.itforum.service.PostService
import com.example.itforum.service.ReportAccountService
import com.example.itforum.service.SkillService
import com.example.itforum.service.TagService
import com.example.itforum.user.login.otp.AuthService

object RetrofitInstance {
    private const val SECOND_URL  = "http://192.168.1.216:4000"
    private const val URL_Phone = "http://192.168.1.172:4000"
    //    private const val BASE_URL = "https://beitforum-b0ng.onrender.com/"
//    private const val SECOND_URL = "https://192.168.1.216:4000"

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    val retrofit = Retrofit.Builder()
//        .baseUrl(BASE_URL)
        .baseUrl(URL_Phone)
//        .baseUrl(SECOND_URL)
        .client(okHttpClient) // ← Đảm bảo dùng client có timeout
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val reportAccountService: ReportAccountService by lazy { retrofit.create(ReportAccountService::class.java) }
    val userService: UserService by lazy {retrofit.create(UserService::class.java) }
    val reportPostService: ReportPostService by lazy { retrofit.create(ReportPostService::class.java) }
    val newsService: NewsService by lazy { retrofit.create(NewsService::class.java) }
    val postService: PostService by lazy {retrofit.create(PostService::class.java) }
    val complaintService: ComplaintService by lazy {retrofit.create(ComplaintService::class.java) }
    val aiCrashService: AiCrashService by lazy { retrofit.create(AiCrashService::class.java) }
    val notificationService: NotificationService by lazy { retrofit.create(NotificationService::class.java) }
    val skillService: SkillService by lazy { retrofit.create(SkillService::class.java) }
    val api: AnalyticsApi by lazy {
        Retrofit.Builder()
//            .baseUrl(SECOND_URL)
            .baseUrl(URL_Phone)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnalyticsApi::class.java)
    }

val followService: FollowService by lazy { retrofit.create(FollowService::class.java) }
    val authService: AuthService by lazy { retrofit.create(AuthService::class.java) }
    val tagService: TagService by lazy { retrofit.create(TagService::class.java) }
}
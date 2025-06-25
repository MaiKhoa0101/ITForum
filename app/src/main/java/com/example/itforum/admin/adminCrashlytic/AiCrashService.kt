package com.example.itforum.admin.adminCrashlytic
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AiCrashService {
    @POST("analyzeCrash")
    fun analyzeCrash(@Body crashLog: CrashLogSend): Call<Any>
}

data class CrashLogSend(
    val email: String,
    val userId: String,
    val error: String
)
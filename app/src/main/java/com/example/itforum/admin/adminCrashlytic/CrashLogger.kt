package com.example.itforum.admin.adminCrashlytic


import com.example.itforum.retrofit.RetrofitInstance
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



data class CrashLog(
    val aiSummary: String = "",
    val email: String = "",
    val userId: String? = null,
    val error: String = "",
    val timestamp: Long = System.currentTimeMillis()
)


object CrashLogger {
    fun logCrash(error: Throwable, userId: String, email: String) {
        try {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.setUserId(userId)
            crashlytics.setCustomKey("email", email)
            crashlytics.recordException(error)

            val crashLog = CrashLogSend(email, userId, error.stackTraceToString())

            RetrofitInstance.aiCrashService.analyzeCrash(crashLog)
                .enqueue(object : Callback<Any> {
                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        println("Gửi crash đến GPT API thành công.")
                    }

                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        println("Lỗi gửi crash: ${t.message}")
                    }
                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}





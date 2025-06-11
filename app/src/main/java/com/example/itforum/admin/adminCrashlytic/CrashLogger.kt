package com.example.itforum.admin.adminCrashlytic


import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

//
//data class CrashLog(
//    val email: String,
//    val userId: String,
//    val error: String,
//    val timestamp: Long = System.currentTimeMillis()
//)
data class CrashLog(
    val email: String = "",
    val userId: String? = null,
    val error: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

object CrashLogger {

    suspend fun logCrash(error: Throwable, userId: String, email: String) {
        try {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.setUserId(userId)
            crashlytics.setCustomKey("email", email)
            crashlytics.log("Crash from $userId - $email")
            crashlytics.recordException(error)

            val crashLog = CrashLog(email, userId, error.stackTraceToString())

            // ✅ await để tránh NetworkOnMainThreadException
            FirebaseFirestore.getInstance()
                .collection("crash_logs")
                .document("latest_crash_$userId")
                .set(crashLog)
                .await() // ✅ Đây là phần bạn thiếu!
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}





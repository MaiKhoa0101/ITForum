package com.example.itforum.admin.adminCrashlytic


import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore

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

        // Overload: gọi có truyền email + userId (chính xác nhất)
        suspend fun logCrash(error: Throwable, userId: String, email: String) {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.setUserId(userId)
            crashlytics.setCustomKey("email", email)
            crashlytics.log("Crash from $userId - $email")
            crashlytics.recordException(error)

            val crashLog = CrashLog(email, userId, error.stackTraceToString())
            FirebaseFirestore.getInstance()
                .collection("crash_logs")
                .document("latest_crash_$userId")
                .set(crashLog)

            kotlinx.coroutines.delay(3000)
            throw RuntimeException("Crash for testing")
        }

    }





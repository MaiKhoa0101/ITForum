package com.example.itforum.user.root

import android.app.Application
import com.example.itforum.admin.adminCrashlytic.CrashLogger
import com.example.itforum.admin.adminCrashlytic.UserSession
import com.example.itforum.admin.adminCrashlytic.UserSession.email
import com.example.itforum.admin.adminCrashlytic.UserSession.userId
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        //  Khởi tạo Firebase nếu chưa có
        FirebaseApp.initializeApp(this)

        // Cho phép Crashlytics thu thập lỗi
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        FirebaseCrashlytics.getInstance().setUserId(UserSession.userId)
        FirebaseCrashlytics.getInstance().setCustomKey("email", UserSession.email)


        //  Bắt crash toàn cục và ghi log
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runBlocking {
                CrashLogger.logCrash(
                    throwable,
                    userId = UserSession.userId,
                    email = UserSession.email
                )
            }
        }

    }
}

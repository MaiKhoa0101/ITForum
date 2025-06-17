package com.example.itforum.user.Analytics


import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics



fun logScreenEnter(context: Context, screenName: String) {
    val enterTime = System.currentTimeMillis()
    context.getSharedPreferences("analytics", Context.MODE_PRIVATE)
        .edit()
        .putLong("enter_time_$screenName", enterTime)
        .apply()

    val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    val bundle = Bundle().apply {
        putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
    }
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    Log.d("Analytics", "ENTER: $screenName at $enterTime")
}

fun logScreenExit(context: Context, screenName: String) {
    val prefs = context.getSharedPreferences("analytics", Context.MODE_PRIVATE)
    val enterTime = prefs.getLong("enter_time_$screenName", 0L)
    val durationSeconds = if (enterTime > 0) (System.currentTimeMillis() - enterTime) / 1000 else 0

    val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    val bundle = Bundle().apply {
        putString("screen_name", screenName)
        putLong("duration_seconds", durationSeconds)
    }
    firebaseAnalytics.logEvent("screen_exit", bundle)
    Log.d("Analytics", "EXIT: $screenName after $durationSeconds seconds")
}


//package com.example.itforum.user.Analytics
//
//
//
//import android.content.Context
//import android.os.Bundle
//import com.google.firebase.analytics.FirebaseAnalytics
//
//fun logScreenView(context: Context, screenName: String) {
//    val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
//    val bundle = Bundle().apply {
//        putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
//        putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity") // hoặc tên activity thực tế của bạn
//    }
//    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
//}

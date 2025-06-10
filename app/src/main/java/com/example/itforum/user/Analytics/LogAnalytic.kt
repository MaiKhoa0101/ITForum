package com.example.itforum.user.Analytics



import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

fun logScreenView(context: Context, screenName: String) {
    val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    val bundle = Bundle().apply {
        putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity") // hoặc tên activity thực tế của bạn
    }
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
}

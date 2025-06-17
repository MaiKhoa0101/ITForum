package com.example.itforum.user.login.loginGoogle



import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.content.SharedPreferences

fun handleGoogleLogin(
    context: Context,
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController,
    result: ActivityResult
) {
    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
    try {
        val account = task.getResult(ApiException::class.java)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        FirebaseAuth.getInstance().signOut()

        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val user = authTask.result?.user
                    user?.let {
                        sharedPreferences.edit()
                            .putString("loginType", "google")
                            .putString("uid", it.uid)
                            .putString("email", it.email ?: "") // ✅ fallback nếu email null
                            .apply()

                        navHostController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                    } ?: run {
                        Log.e("GoogleLogin", "User null sau khi đăng nhập")
                    }

                } else {
                    Log.e("GoogleLogin", "Xác thực thất bại", authTask.exception)
                }
            }
    } catch (e: ApiException) {
        Log.e("GoogleLogin", "Đăng nhập Google thất bại", e)
    }
}

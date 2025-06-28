package com.example.itforum.user.login.loginGoogle

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun GoogleSignInButton(onTokenReceived: (FirebaseUser?) -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity ?: return

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d("GoogleLogin", "✅ Google account: ${account?.email}")

            val idToken = account.idToken
            if (idToken == null) {
                Log.e("GoogleLogin", "❌ ID token is null! Kiểm tra .requestIdToken(...)")
                onTokenReceived(null)
                return@rememberLauncherForActivityResult
            }

            Log.d("GoogleLogin", "✅ ID Token: $idToken")

            val credential = GoogleAuthProvider.getCredential(idToken, null)

            FirebaseAuth.getInstance().signOut()

            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        val firebaseUser = authTask.result?.user
                        Log.d("GoogleLogin", "✅ Firebase login success: ${firebaseUser?.email}")
                        onTokenReceived(firebaseUser)
                    } else {
                        Log.e("GoogleLogin", "❌ Firebase sign-in failed", authTask.exception)
                        onTokenReceived(null)
                    }
                }

        } catch (e: ApiException) {
            Log.e("GoogleLogin", "❌ Google Sign-In failed: ${e.statusCode} – ${e.message}", e)
            onTokenReceived(null)
        }
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            FirebaseAuth.getInstance().signOut()

            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        val firebaseUser = authTask.result?.user
                        println("firebaseUser: $firebaseUser")

                        onTokenReceived(firebaseUser) // Gọi callback tiếp tục xử lý
                    } else {
                        onTokenReceived(null)
                    }
                }

        } catch (e: ApiException) {
            onTokenReceived( null)
        }
    }

    Button(
        onClick = {
            val client = getGoogleSignInClient(activity)
            launcher.launch(client.signInIntent)
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
        border = BorderStroke(1.dp, Color.Gray),
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(50.dp)
    ) {
        Text("Đăng nhập bằng Google", color = Color.Black)
    }
}

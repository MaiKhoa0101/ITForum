package com.example.itforum.user.login.loginGoogle

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
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
fun GoogleSignInButton(sharedPreferences: SharedPreferences,onResult: (FirebaseUser?) -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity ?: return

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            FirebaseAuth.getInstance().signOut()

            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        val firebaseUser = authTask.result?.user
                        val email = firebaseUser?.email ?: "unknown@gmail.com" // ✅ lấy email Google

                        // Lưu vào SharedPreferences (nếu bạn muốn)
                        val prefs = activity.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                        prefs.edit().putString("email", email).apply()

                        sharedPreferences.edit()
                            .putString("loginType", "google")
                            .apply()

                        onResult(firebaseUser) // Gọi callback tiếp tục xử lý
                    } else {
                        onResult(null)
                    }
                }

        } catch (e: ApiException) {
            onResult(null)
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

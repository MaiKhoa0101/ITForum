

package com.example.itforum.user.login.loginGoogle

import android.content.Context
import com.example.itforum.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id)) // từ strings.xml
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}

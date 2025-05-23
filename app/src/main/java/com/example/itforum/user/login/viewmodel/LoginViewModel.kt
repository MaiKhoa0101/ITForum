package com.example.itforum.user.login.viewmodel


import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.auth0.android.jwt.JWT
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.model.request.LoginUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginViewModel(private var sharedPreferences: SharedPreferences)  : ViewModel() {
    val isSuccess = mutableStateOf(false)

    fun userLogin(emailOrPhone: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val loginUser = LoginUser(emailOrPhone, password)
                val response = RetrofitInstance.userService.login(loginUser)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.accessToken?.let { token ->
                            handleToken(token)
                            isSuccess.value = true
                        } ?: run {
                            showError("Token is empty")
                        }
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                        showError("Login failed: $errorMsg")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("Network or unexpected error: ${e.message}")
                }
            }
        }
    }

    // Xử lý token sau khi login thành công
    private fun handleToken(token: String) {
        saveToken(token)

        try {
            val jwt = JWT(token)
            val role = jwt.getClaim("role").asString()
            // TODO: Xử lý role nếu cần
        } catch (e: Exception) {
            showError("Invalid token format")
        }
    }

    private fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString("access_token", token)
            .apply()
    }



    private fun showError(message: String) {
        // TODO: Hiển thị lỗi lên UI, ví dụ Toast, Snackbar, hoặc cập nhật LiveData
        Log.e("LoginViewModel", message)
    }

    fun resetLoginState() {
        isSuccess.value = false
    }
}
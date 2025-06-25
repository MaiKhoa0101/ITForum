package com.example.itforum.user.login.viewmodel


import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.example.itforum.admin.adminCrashlytic.CrashLogger
import com.example.itforum.admin.adminCrashlytic.UserSession
import com.example.itforum.retrofit.RetrofitInstance


import com.example.itforum.retrofit.RetrofitInstance.userService
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.modelData.request.LoginUser
import com.example.itforum.user.modelData.request.RegisterUser

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException

import kotlinx.coroutines.tasks.await
import okhttp3.ResponseBody.Companion.toResponseBody


import org.json.JSONObject



class LoginViewModel(private var sharedPreferences: SharedPreferences)  : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()



    fun userLogin(emailOrPhone: String, password: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val fcmToken = FirebaseMessaging.getInstance().token.await()
            try {
                val loginUser = LoginUser(emailOrPhone, password, fcmToken )
                println("Login ne" + loginUser)
                val response = userService.login(loginUser)
                Log.d("LoginViewModel", "Response: $response")
                Log.d("LoginViewModel", "Response: ${response.body()}")
                if (response.isSuccessful) {

                    val token = response.body()?.accessToken
                    saveUserEmail(emailOrPhone)
                    if (!token.isNullOrEmpty()) {
                        println("Dang nhap thanh cong voi token ko null va response "+response.body())
                        handleToken(token)
                        _uiState.value = UiState.Success(
                            response.body()?.message ?: "Đăng nhập thành công"
                        )
                        delay(500) // Cho phép UI xử lý trạng thái Success

                    } else {
                        showError("Token không hợp lệ")
                        _uiState.value = response.body()?.let { UiState.Error(it.message) }!!
                        delay(500) // Cho phép UI xử lý trạng thái Success
                    }
                } else {
                    _uiState.value = UiState.Error(response.message())
                    delay(500) // Cho phép UI xử lý trạng thái Success
                }
            } catch (e: IOException) {
                _uiState.value = UiState.Error("Lỗi kết nối mạng: ${e.localizedMessage}")
                showError("Không thể kết nối máy chủ, vui lòng kiểm tra mạng.")
                delay(500) // Cho phép UI xử lý trạng thái Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Lỗi hệ thống, vui lòng thử lại")
                showError("Lỗi mạng hoặc bất ngờ: ${e.localizedMessage ?: "Không rõ"}")
                delay(500) // Cho phép UI xử lý trạng thái Success
            }
            delay(500)
            _uiState.value = UiState.Idle

        }
    }

    fun handleGoogleLoginWithToken(firebaseUser: FirebaseUser) {
        viewModelScope.launch {
            try {
                val name = firebaseUser.displayName ?: "unknown"
                val email = firebaseUser.email ?: "unknown@gmail.com"
                val password = "12345678"
                val phone = firebaseUser.phoneNumber
                val fcmToken = FirebaseMessaging.getInstance().token.await()

                val registerRequest = RegisterUser(name = name, email = email,phone = phone,password= password)
                println("RegisterRequest: "+registerRequest)
                val loginRequest = LoginUser(email, password, fcmToken)
                println("LoginRequest: "+loginRequest)

                val registerResponse = userService.register(registerRequest)
                println("RegisterResponse:  "+registerResponse)
                println("RegisterResponseError:  "+registerResponse.message())
                if (registerResponse.isSuccessful || registerResponse.message()=="Unauthorized" ) {
                    println(registerResponse)
                    val loginResponse = userService.login(loginRequest)
                    if (loginResponse.isSuccessful) {
                        val token = loginResponse.body()?.accessToken
                        handleToken(token!!)
                        _uiState.value = UiState.Success(
                            loginResponse.body()?.message ?: "Đăng nhập thành công"
                        )
                        delay(500) // Cho phép UI xử lý trạng thái Success
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = UiState.Error("Lỗi khi xử lý đăng nhập Google: ${e.localizedMessage}")
            }
        }
    }




    private fun handleToken(token: String) {
        saveToken(token)
        try {
            val jwt = JWT(token)
            val id = jwt.getClaim("userId").asString()
            val role = jwt.getClaim("role").asString()
            val email = jwt.getClaim("email").asString()
            UserSession.load(email ?: "", id ?: "")


            saveUserEmail(email)
            saveUserId(id)
            saveUserRole(role)


        } catch (e: Exception) {
            showError("Invalid token format "+e)
        }

    }

    private fun saveUserEmail(email: String?) {
        sharedPreferences.edit()
            .putString("email", email)
            .apply()
    }


    private fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString("access_token", token)
            .apply()
    }
    private fun saveUserId(userId: String?) {
        sharedPreferences.edit()
            .putString("userId", userId)
            .apply()
    }
    private fun saveUserRole(role: String?) {
        sharedPreferences.edit()
            .putString("role", role)
            .apply()
    }




    private fun showError(message: String) {
        _uiState.value = UiState.Error(message)
        Log.e("LoginViewModel", message)
    }


}

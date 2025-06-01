package com.example.itforum.user.login.viewmodel


import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.model.request.LoginUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException


class LoginViewModel(private var sharedPreferences: SharedPreferences)  : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun userLogin(emailOrPhone: String, password: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val loginUser = LoginUser(emailOrPhone, password)
                val response = RetrofitInstance.userService.login(loginUser)

                Log.d("LoginViewModel", "Response: $response")
                if (response.isSuccessful) {
                    val token = response.body()?.accessToken
                    if (!token.isNullOrEmpty()) {
                        handleToken(token)
                        _uiState.value = UiState.Success(
                            response.body()?.message ?: "Đăng nhập thành công"
                        )
                        delay(2000)
                        _uiState.value = UiState.Idle
                    } else {
                        showError("Token không hợp lệ")
                        _uiState.value = UiState.Error(response.message())
                    }
                } else {
                    showError(response.message())
                }
            } catch (e: IOException) {
                _uiState.value = UiState.Error("Lỗi kết nối mạng: ${e.localizedMessage}")
                showError("Không thể kết nối máy chủ, vui lòng kiểm tra mạng.")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Lỗi hệ thống, vui lòng thử lại")
                showError("Lỗi mạng hoặc bất ngờ: ${e.localizedMessage ?: "Không rõ"}")
            }
        }
    }


    // Xử lý token sau khi login thành công
    private fun handleToken(token: String) {
        saveToken(token)
        try {
            val jwt = JWT(token)
            val id = jwt.getClaim("userId").asString()
            saveUserId(id)
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
    private fun saveUserId(userId: String?) {
        sharedPreferences.edit()
            .putString("userId", userId)
            .apply()
    }



    private fun showError(message: String) {
        _uiState.value = UiState.Error(message)
        Log.e("LoginViewModel", message)
    }


}
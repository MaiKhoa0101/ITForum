package com.example.itforum.user.register.viewmodel


import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.modelData.request.RegisterUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import androidx.lifecycle.viewModelScope
import com.example.itforum.user.effect.model.UiState
import okio.IOException

class RegisterViewModel(private var sharedPreferences: SharedPreferences)  : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun register(registerUser: RegisterUser) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = RetrofitInstance.userService.register(registerUser)
                if (response.isSuccessful) {
                    _uiState.value = UiState.Success(
                        response.body()?.message ?: "Đăng ký thành công"
                    )
                    delay(500)
                } else {
                    showError("Đăng ký thất bại: ${response.message()}")
                    _uiState.value = UiState.Error(response.message())
                    delay(500)
                }
            } catch (e: IOException) {
                _uiState.value = UiState.Error("Lỗi phản hồi từ server")
                showError("Không thể kết nối máy chủ, vui lòng kiểm tra mạng.")
                delay(500)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Lỗi không xác định: ${e.message}")
                showError("Lỗi không xác định: ${e.localizedMessage ?: "Không rõ"}")
                delay(500)
            }
            _uiState.value = UiState.Idle
        }
    }


    private fun showError(message: String) {
        // TODO: Hiển thị lỗi lên UI, ví dụ Toast, Snackbar, hoặc cập nhật LiveData
        Log.e("Register", message)
    }

}
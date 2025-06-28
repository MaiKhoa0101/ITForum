package com.example.itforum.user.notification.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.modelData.request.NewsRequest
import com.example.itforum.user.modelData.request.NotificationRequest
import com.example.itforum.user.modelData.response.Notification
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.IOException

class NotificationViewModel (
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _notificationList = MutableStateFlow<List<Notification>>(emptyList())
    val notificationList: StateFlow<List<Notification>> = _notificationList

    private val _notification = MutableStateFlow<Notification?>(null)
    val notification: StateFlow<Notification?> = _notification

    fun getNotification(userId: String){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.notificationService.getNotifications(userId)
                if (response.isSuccessful) {
                    println("Tim thong bao thanh cong")
                    val notifications = response.body()
                    if (notifications != null) {
                        _notificationList.value = notifications.data
                        _uiState.value = UiState.Success("Notifications fetched successfully")
                        println("notifications Viewmodel: $notifications")
                    } else {
                        _uiState.value = UiState.Error("No notifications found")
                    }
                } else {
                    _uiState.value = UiState.Error("Failed to fetch notifications")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun readNotification(id: String){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.notificationService.readNotification(id)
                if (response.isSuccessful) {
                    println("Da doc thong bao thanh cong")
                } else {
                    _uiState.value = UiState.Error("Failed to fetch notifications")
                }
                } catch (e: Exception) {
                _uiState.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun getAllNotifications(){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.notificationService.getAllNotifications()
                if (response.isSuccessful) {
                    println("Lấy tất cả thông báo thành công")
                    val notifications = response.body()
                    if (notifications != null) {
                        _notificationList.value = notifications.listNotification
                        _uiState.value = UiState.Success("Notifications fetched successfully")
                        println("notifications Viewmodel: $notifications")
                    } else {
                        _uiState.value = UiState.Error("No notifications found")
                    }
                } else {
                    _uiState.value = UiState.Error("Failed to fetch notifications")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun createNotificationTopic(notificationRequest: NotificationRequest) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                Log.d("NotificationViewModel", "Request: $notificationRequest")
                val response = RetrofitInstance.notificationService.createNotificationTopic(notificationRequest)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("NotificationViewModel", "Success Response: $responseBody")
                    _uiState.value = UiState.Success(
                        responseBody?: "Tạo thông báo thành công"
                    )
                    delay(500) // Cho Compose thời gian phản ứng trước khi đổi trạng thái
                    _uiState.value = UiState.Idle
                } else {
                    // Get error details from response body
                    val errorBody = response.errorBody()?.string()
                    Log.e("NotificationViewModel", "Error Response Body: $errorBody")

                    val errorMessage = when (response.code()) {
                        400 -> "Dữ liệu không hợp lệ: $errorBody"
                        404 -> "Không tìm thấy người dùng"
                        500 -> "Lỗi server: $errorBody"
                        else -> "Lỗi không xác định (${response.code()}): $errorBody"
                    }

                    showError(errorMessage)
                    _uiState.value = UiState.Error(errorMessage)
                }
            } catch (e: java.io.IOException) {
                val errorMsg = "Lỗi kết nối mạng: ${e.localizedMessage}"
                Log.e("NotificationViewModel", errorMsg, e)
                _uiState.value = UiState.Error(errorMsg)
                showError("Không thể kết nối máy chú, vui lòng kiểm tra mạng.")
            } catch (e: Exception) {
                val errorMsg = "Lỗi hệ thống: ${e.message ?: e.localizedMessage}"
                Log.e("NotificationViewModel", errorMsg, e)
                _uiState.value = UiState.Error(errorMsg)
                showError("Lỗi bất ngờ: ${e.localizedMessage ?: "Không rõ"}")
            }
        }
    }

    fun getNotificationById(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = RetrofitInstance.notificationService.getNotificationById(id)

                if (response.isSuccessful) {
                    _uiState.value = UiState.FetchSuccess("Lấy khiếu nại ${id} thành công")
                    _notification.value  = response.body()
                    delay(500)
                    _uiState.value = UiState.Idle
                    println("UI state value is idle")
                }
                else {
                    showError("Response get không hợp lệ")
                    _uiState.value = UiState.FetchFail(response.message())
                }
            }
            catch (e: IOException) {
                _uiState.value = UiState.FetchFail("Lỗi kết nối mạng: ${e.localizedMessage}")
                showError("Không thể kết nối máy chủ, vui lòng kiểm tra mạng.")
            }
            catch (e: Exception) {
                _uiState.value = UiState.FetchFail(e.message ?: "Lỗi hệ thống, vui lòng thử lại")
                showError("Lỗi mạng hoặc bất ngờ: ${e.localizedMessage ?: "Không rõ"}")
            }

        }
    }

    private fun showError(message: String) {
        _uiState.value = UiState.Error(message)
        Log.e("NotificationViewModel", message)
    }
}

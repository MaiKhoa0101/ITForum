package com.example.itforum.user.notification.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.modelData.response.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel (
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private val _notificationList = MutableStateFlow<List<Notification>>(emptyList())
    val notificationList: StateFlow<List<Notification>> = _notificationList

    fun getNotification(userId: String){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.notificationService.getNotifications(userId)
                if (response.isSuccessful()) {
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
                if (response.isSuccessful()) {
                    println("Da doc thong bao thanh cong")
                } else {
                    _uiState.value = UiState.Error("Failed to fetch notifications")
                }
                } catch (e: Exception) {
                _uiState.value = UiState.Error("Error: ${e.message}")
            }
        }

    }
}

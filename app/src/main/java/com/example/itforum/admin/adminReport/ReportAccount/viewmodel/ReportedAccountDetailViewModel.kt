package com.example.itforum.admin.adminReport.ReportAccount.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.admin.adminReport.ReportAccount.model.request.BanUserRequest
import com.example.itforum.admin.adminReport.ReportAccount.model.response.BanUserResponse
import com.example.itforum.admin.adminReport.ReportAccount.model.response.ReportedAccountResponse
import com.example.itforum.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportedAccountDetailViewModel(
    private val repository: ReportRepository
) : ViewModel() {

    private val _detail = MutableStateFlow<ReportedAccountResponse?>(null)
    val detail: StateFlow<ReportedAccountResponse?> = _detail

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadReportedUserDetail(reportId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getReportedUserDetail(reportId)
                Log.d("DETAIL", "Code: ${response.code()}, Body: ${response.body()}")

                if (response.isSuccessful) {
                    _detail.value = response.body()
                } else {
                    _error.value = "Lỗi response: ${response.code()}"
                    Log.e("DETAIL", "Lỗi response không thành công: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("DETAIL", "Exception: ${e.message}")
            }
        }
    }
    fun banUser(userId: String, durationInDays: Int) {
        viewModelScope.launch {
            try {
                val response = repository.banUser(userId, BanUserRequest(durationInDays))
                if (response.isSuccessful) {
                    println(" Khóa thành công: ${response.body()?.message}")

                } else {
                    println(" Lỗi: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println(" Exception khi khóa: ${e.message}")
            }
        }
    }

    fun unbanUser(userId: String) {
        viewModelScope.launch {
            try {
                val response = repository.updateUser(userId, mapOf("isBanned" to false, "bannedUntil" to null))
                if (response.isSuccessful) {
                    println(" Bỏ chặn thành công")
                    loadReportedUserDetail(reportId = userId)
                }
            } catch (e: Exception) {
                println(" Exception khi bỏ chặn: ${e.message}")
            }
        }
    }
}

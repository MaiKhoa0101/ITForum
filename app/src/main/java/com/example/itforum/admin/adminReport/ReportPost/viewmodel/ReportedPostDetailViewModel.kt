package com.example.itforum.admin.adminReport.ReportPost.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.admin.adminReport.ReportAccount.model.request.BanUserRequest
import com.example.itforum.admin.adminReport.ReportPost.model.response.ReportedPostDetailResponse
import com.example.itforum.repository.ReportPostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportedPostDetailViewModel(
    private val repository: ReportPostRepository
) : ViewModel() {

    private val _detail = MutableStateFlow<ReportedPostDetailResponse?>(null)
    val detail: StateFlow<ReportedPostDetailResponse?> = _detail

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadReportedPostById(id: String) {
        viewModelScope.launch {
            try {
                val response = repository.getPostDetailByReportId(id)
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
}



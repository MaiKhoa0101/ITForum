package com.example.itforum.admin.adminReport.ReportPost.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.itforum.admin.adminReport.ReportPost.model.request.ReportedPost
import com.example.itforum.admin.adminReport.ReportPost.model.response.ReportedPostDetailResponse
import com.example.itforum.admin.adminReport.ReportPost.model.response.ReportedPostList
import com.example.itforum.repository.ReportPostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportedPostViewModel(
    private val repository: ReportPostRepository
) : ViewModel() {

    private val _Posts = MutableStateFlow<List<ReportedPostList>>(emptyList())
    val Posts: StateFlow<List<ReportedPostList>> = _Posts
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadReportedPosts() {
        viewModelScope.launch {
            try {
                val response = repository.getAllReportedPosts()
                if (response.isSuccessful) {
                    val response = repository.getAllReportedPosts()
                    if (response.isSuccessful) {
                        _Posts.value = response.body() ?: emptyList() // Không cần map nữa 🎉
                    }

                } else {
                    _error.value = "API lỗi: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    // Lọc danh sách báo cáo
    private val _detail = MutableStateFlow<ReportedPostDetailResponse?>(null)
    val detail: StateFlow<ReportedPostDetailResponse?> = _detail

    fun loadReportedPostDetail(reportId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getPostDetailByReportId(reportId)
                if (response.isSuccessful) {
                    _detail.value = response.body()
                } else {
                    _error.value = "API lỗi: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message

                }
        }
    }
}


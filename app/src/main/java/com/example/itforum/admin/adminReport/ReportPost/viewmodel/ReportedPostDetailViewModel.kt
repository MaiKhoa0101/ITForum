package com.example.itforum.admin.adminReport.ReportPost.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.admin.adminReport.ReportPost.model.request.ReportedPostDetail
import com.example.itforum.admin.adminReport.ReportPost.model.request.ReportedPostDetailResponse
import com.example.itforum.repository.ReportPostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportedPostDetailViewModel(
    private val repository: ReportPostRepository
) : ViewModel() {

    private val _detail = MutableStateFlow<ReportedPostDetailResponse?>(null)
    val detail: StateFlow<ReportedPostDetailResponse?> = _detail


    fun loadReportedPostById(id: String) {
        viewModelScope.launch {
            try {
                val result = repository.getPostDetailByReportId(id)
                _detail.value = result
            } catch (e: Exception) {
                // handle error
            }
        }
    }
}

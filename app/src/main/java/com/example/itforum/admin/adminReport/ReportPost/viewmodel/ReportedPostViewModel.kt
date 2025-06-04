package com.example.itforum.admin.adminReport.ReportPost.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.itforum.admin.adminReport.ReportPost.model.request.ReportedPost
import com.example.itforum.repository.ReportPostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportedPostViewModel(
    private val repository: ReportPostRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<ReportedPost>>(emptyList())
    val posts = _posts.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadReportedPosts() {
        viewModelScope.launch {
            try {
                _posts.value = repository.getAllReportedPosts()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}

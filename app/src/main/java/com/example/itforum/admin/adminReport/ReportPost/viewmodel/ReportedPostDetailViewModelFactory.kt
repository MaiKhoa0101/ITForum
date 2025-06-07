package com.example.itforum.admin.adminReport.ReportPost.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.itforum.repository.ReportPostRepository

class ReportedPostDetailViewModelFactory(
    private val repository: ReportPostRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReportedPostDetailViewModel(repository) as T
    }
}
package com.example.itforum.admin.adminReport.ReportPost.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.itforum.repository.ReportPostRepository

class ReportedPostViewModelFactory(
    private val repository: ReportPostRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportedPostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportedPostViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

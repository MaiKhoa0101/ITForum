package com.example.itforum.admin.adminReport.ReportPost.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.itforum.repository.ReportPostRepository

class ReportedPostViewModelFactory(
    private val repository: ReportPostRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ReportedPostViewModel::class.java) -> {
                ReportedPostViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ReportedPostDetailViewModel::class.java) -> {
                ReportedPostDetailViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

}

package com.example.itforum.admin.adminReport.ReportAccount.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.itforum.repository.ReportRepository

class ReportedAccountDetailViewModelFactory(
    private val repository: ReportRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReportedAccountDetailViewModel(repository) as T
    }
}


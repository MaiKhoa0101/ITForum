package com.example.itforum.admin.adminReport.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.admin.adminReport.common.model.BaseReport
import com.example.itforum.admin.adminReport.common.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BaseReportViewModel<T: BaseReport>(
    private val repository: ReportRepository<T>
): ViewModel() {
    private val _reports = MutableStateFlow<List<T>>(emptyList())
    val reports: StateFlow<List<T>>  = _reports
    private val _error = MutableStateFlow<T?>(null)
    val error: StateFlow<T?> = _error
    fun loadReports() {
        viewModelScope.launch {
            try {
                _reports.value = repository.getAll()
            } catch (e: Exception) {
                _error.value = e.message as T
            }
    }
}
    fun getReportById(id: String): T? = _reports.value.find { it._id == id }
}
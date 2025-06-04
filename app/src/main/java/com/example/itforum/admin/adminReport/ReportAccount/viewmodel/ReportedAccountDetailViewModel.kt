package com.example.itforum.admin.adminReport.ReportAccount.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.admin.adminReport.ReportAccount.model.response.ReportedAccountResponse
import com.example.itforum.admin.adminReport.ReportAccount.model.response.ReportedAccountUIModel
import com.example.itforum.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportedAccountDetailViewModel(
    private val repository: ReportRepository
) : ViewModel() {

    private val _detail = MutableStateFlow<ReportedAccountUIModel?>(null)
    val detail: StateFlow<ReportedAccountUIModel?> = _detail

    fun loadReportedUserDetail(reportId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getReportedUserDetail(reportId)
                Log.d("DETAIL", "Code: ${response.code()}, Body: ${response.body()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        _detail.value = ReportedAccountUIModel(
                            reportedUserId = it.reportedUserId,
                            reportedUserName = it.reportedUserName,
                            email = it.email,
                            phone = it.phone,
                            isBanned = it.isBanned,
                            reporterName = it.reporterName,
                            reason = it.reason,
                            createdAt = it.createdAt
                        )
                    }
                } else {
                    Log.e("DETAIL", "Lỗi response không thành công")
                }
            } catch (e: Exception) {
                Log.e("DETAIL", "Exception: ${e.message}")
            }
        }
    }

}



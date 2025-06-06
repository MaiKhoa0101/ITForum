package com.example.itforum.admin.adminComplaint.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.modelData.response.Complaint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class ComplaintViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _complaint = MutableStateFlow<Complaint?>(null)
    val complaint: StateFlow<Complaint?>
        get() = _complaint

    private val _listComplaint = MutableStateFlow<List<Complaint>?>(null)
    val listComplaint: StateFlow<List<Complaint>?>
        get() = _listComplaint

    fun getComplaint() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = RetrofitInstance.complaintService.getComplaint()

                if (response.isSuccessful) {
                    _uiState.value = UiState.FetchSuccess(
                        response.body()?.message ?: "Lấy khiếu nại thành công"
                    )
                    _listComplaint.value  = response.body()?.listComplaint
                    delay(500)
                    _uiState.value = UiState.Idle
                    println("UI state value is idle")
                }
                else {
                    showError("Response get không hợp lệ")
                    _uiState.value = UiState.FetchFail(response.message())                }
            }
            catch (e: IOException) {
                _uiState.value = UiState.FetchFail("Lỗi kết nối mạng: ${e.localizedMessage}")
                showError("Không thể kết nối máy chủ, vui lòng kiểm tra mạng.")
            }
            catch (e: Exception) {
                _uiState.value = UiState.FetchFail(e.message ?: "Lỗi hệ thống, vui lòng thử lại")
                showError("Lỗi mạng hoặc bất ngờ: ${e.localizedMessage ?: "Không rõ"}")
            }

        }
    }

    fun getByIdComplaint(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = RetrofitInstance.complaintService.getComplaintById(id)

                if (response.isSuccessful) {
                    _uiState.value = UiState.FetchSuccess("Lấy khiếu nại ${id} thành công")
                    _complaint.value  = response.body()
                    delay(500)
                    _uiState.value = UiState.Idle
                    println("UI state value is idle")
                }
                else {
                    showError("Response get không hợp lệ")
                    _uiState.value = UiState.FetchFail(response.message())                }
            }
            catch (e: IOException) {
                _uiState.value = UiState.FetchFail("Lỗi kết nối mạng: ${e.localizedMessage}")
                showError("Không thể kết nối máy chủ, vui lòng kiểm tra mạng.")
            }
            catch (e: Exception) {
                _uiState.value = UiState.FetchFail(e.message ?: "Lỗi hệ thống, vui lòng thử lại")
                showError("Lỗi mạng hoặc bất ngờ: ${e.localizedMessage ?: "Không rõ"}")
            }

        }
    }



    private fun showError(message: String) {
        _uiState.value = UiState.Error(message)
        Log.e("ComplaintViewModel", message)
    }
}
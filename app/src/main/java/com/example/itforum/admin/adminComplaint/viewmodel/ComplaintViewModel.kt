package com.example.itforum.admin.adminComplaint.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.modelData.request.ComplaintRequest
import com.example.itforum.user.modelData.response.Complaint
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class ComplaintViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _complaint = MutableStateFlow<Complaint?>(null)
    val complaint: StateFlow<Complaint?>
        get() = _complaint

    private val _uiStateCreate = MutableStateFlow<UiState>(UiState.Idle)
    val uiStateCreate: StateFlow<UiState> = _uiStateCreate.asStateFlow()

    private val _listComplaint = MutableStateFlow<List<Complaint>?>(null)
    val listComplaint: StateFlow<List<Complaint>?>
        get() = _listComplaint

    fun getAllComplaint() {
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
                    _uiState.value = UiState.FetchFail(response.message())
                }
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

    fun createComplaint(createComplaintRequest: ComplaintRequest, context: Context) {
        viewModelScope.launch {
            _uiStateCreate.value = UiState.Loading
            try {
                Log.d("ComplaintViewModel", "Request: $createComplaintRequest")

                val img = createComplaintRequest.img?.let {
                    prepareFilePart(context, it, "img")
                }

                // Chỉ tạo MultipartBody.Part cho các trường không null
                val userId = createComplaintRequest.userId.let {
                    MultipartBody.Part.createFormData("userId", it)
                }
                val title = createComplaintRequest.title.let {
                    MultipartBody.Part.createFormData("title", it)
                }
                val reason = createComplaintRequest.reason.let {
                    MultipartBody.Part.createFormData("reason", it)
                }

                val response = img?.let {
                    RetrofitInstance.complaintService.createComplaint(
                        userId = userId,
                        title = title,
                        reason = reason,
                        img = it
                    )
                }
                img?.let {Log.d("Img", img.toString())}

                if (response != null) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("ComplaintViewModel", "Success Response: ${responseBody?.message}")
                        _uiStateCreate.value = UiState.Success(
                            responseBody?.message ?: "Gửi góp ý thành công"
                        )
                        delay(500) // Cho Compose thời gian phản ứng trước khi đổi trạng thái
                        _uiStateCreate.value = UiState.Idle
                    } else {
                        // Get error details from response body
                        val errorBody = response.errorBody()?.string()
                        Log.e("ComplaintViewModel", "Error Response Body: $errorBody")

                        val errorMessage = when (response.code()) {
                            400 -> "Dữ liệu không hợp lệ: $errorBody"
                            404 -> "Không tìm thấy người dùng"
                            500 -> "Lỗi server: $errorBody"
                            else -> "Lỗi không xác định (${response.code()}): $errorBody"
                        }

                        showError(errorMessage)
                        _uiStateCreate.value = UiState.Error(errorMessage)
                    }
                }
            } catch (e: IOException) {
                val errorMsg = "Lỗi kết nối mạng: ${e.localizedMessage}"
                Log.e("ComplaintViewModel", errorMsg, e)
                _uiStateCreate.value = UiState.Error(errorMsg)
                showError("Không thể kết nối máy chú, vui lòng kiểm tra mạng.")
            } catch (e: Exception) {
                val errorMsg = "Lỗi hệ thống: ${e.message ?: e.localizedMessage}"
                Log.e("ComplaintViewModel", errorMsg, e)
                _uiStateCreate.value = UiState.Error(errorMsg)
                showError("Lỗi bất ngờ: ${e.localizedMessage ?: "Không rõ"}")
            }
        }
    }
    private fun prepareFilePart(
        context: Context,
        fileUri: Uri,
        partName: String
    ): MultipartBody.Part? {
        return try {
            val inputStream = context.contentResolver.openInputStream(fileUri)
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, tempFile.name, requestFile)
        } catch (e: Exception) {
            Log.e("ComplaintViewModel", "Error preparing file part", e)
            null
        }
    }

    fun handleRejected(complaintId: String) {
        viewModelScope.launch {
            _uiStateCreate.value = UiState.Loading
            try {
                val response = RetrofitInstance.complaintService.handleRejected(complaintId)

                if (response != null) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("ComplaintViewModel", "Success Response: ${responseBody?.message}")
                        _uiStateCreate.value = UiState.Success(
                            responseBody?.message ?: "Xử lý thành công"
                        )
                        delay(500) // Cho Compose thời gian phản ứng trước khi đổi trạng thái
                        _uiStateCreate.value = UiState.Idle
                    } else {
                        // Get error details from response body
                        val errorBody = response.errorBody()?.string()
                        Log.e("ComplaintViewModel", "Error Response Body: $errorBody")

                        val errorMessage = when (response.code()) {
                            400 -> "Dữ liệu không hợp lệ: $errorBody"
                            404 -> "Không tìm thấy người dùng"
                            500 -> "Lỗi server: $errorBody"
                            else -> "Lỗi không xác định (${response.code()}): $errorBody"
                        }

                        showError(errorMessage)
                        _uiStateCreate.value = UiState.Error(errorMessage)
                    }
                }
            } catch (e: IOException) {
                val errorMsg = "Lỗi kết nối mạng: ${e.localizedMessage}"
                Log.e("ComplaintViewModel", errorMsg, e)
                _uiStateCreate.value = UiState.Error(errorMsg)
                showError("Không thể kết nối máy chú, vui lòng kiểm tra mạng.")
            } catch (e: Exception) {
                val errorMsg = "Lỗi hệ thống: ${e.message ?: e.localizedMessage}"
                Log.e("ComplaintViewModel", errorMsg, e)
                _uiStateCreate.value = UiState.Error(errorMsg)
                showError("Lỗi bất ngờ: ${e.localizedMessage ?: "Không rõ"}")
            }
        }
    }

    fun handleApproved(complaintId: String) {
        viewModelScope.launch {
            _uiStateCreate.value = UiState.Loading
            try {
                val response = RetrofitInstance.complaintService.handleApproved(complaintId)

                if (response != null) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("ComplaintViewModel", "Success Response: ${responseBody?.message}")
                        _uiStateCreate.value = UiState.Success(
                            responseBody?.message ?: "Xử lý thành công"
                        )
                        delay(500) // Cho Compose thời gian phản ứng trước khi đổi trạng thái
                        _uiStateCreate.value = UiState.Idle
                    } else {
                        // Get error details from response body
                        val errorBody = response.errorBody()?.string()
                        Log.e("ComplaintViewModel", "Error Response Body: $errorBody")

                        val errorMessage = when (response.code()) {
                            400 -> "Dữ liệu không hợp lệ: $errorBody"
                            404 -> "Không tìm thấy người dùng"
                            500 -> "Lỗi server: $errorBody"
                            else -> "Lỗi không xác định (${response.code()}): $errorBody"
                        }

                        showError(errorMessage)
                        _uiStateCreate.value = UiState.Error(errorMessage)
                    }
                }
            } catch (e: IOException) {
                val errorMsg = "Lỗi kết nối mạng: ${e.localizedMessage}"
                Log.e("ComplaintViewModel", errorMsg, e)
                _uiStateCreate.value = UiState.Error(errorMsg)
                showError("Không thể kết nối máy chú, vui lòng kiểm tra mạng.")
            } catch (e: Exception) {
                val errorMsg = "Lỗi hệ thống: ${e.message ?: e.localizedMessage}"
                Log.e("ComplaintViewModel", errorMsg, e)
                _uiStateCreate.value = UiState.Error(errorMsg)
                showError("Lỗi bất ngờ: ${e.localizedMessage ?: "Không rõ"}")
            }
        }
    }

    private fun showError(message: String) {
        _uiState.value = UiState.Error(message)
        Log.e("ComplaintViewModel", message)
    }
}
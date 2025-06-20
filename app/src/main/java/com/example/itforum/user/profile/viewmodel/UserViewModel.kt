package com.example.itforum.user.profile.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.modelData.request.UserUpdateRequest
import com.example.itforum.user.modelData.response.UserProfileResponse
import com.example.itforum.user.modelData.response.UserResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import java.io.File

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserViewModel (sharedPreferences: SharedPreferences) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private var userId = sharedPreferences.getString("userId", null)

    init {
        userId = sharedPreferences.getString("userId", null)
    }

    private val _user = MutableStateFlow<UserProfileResponse?>(null)
    val user: StateFlow<UserProfileResponse?> get() = _user

    private val _listUser = MutableStateFlow<List<UserResponse>>(emptyList())
    val listUser: StateFlow<List<UserResponse>> get() = _listUser

    fun getAllUser() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.userService.getAllUser()
                if (response.isSuccessful) {
                    _listUser.value = response.body()!!
                } else {
                    showError("Response get không hợp lệ")
                }
            } catch (e: IOException) {
                showError("Không thể kết nối máy chủ, vui lòng kiểm tra mạng.")
            } catch (e: Exception) {
                showError("Lỗi mạng hoặc bất ngờ: ${e.localizedMessage ?: "Không rõ"}")
            }

        }
    }

    fun getUser() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = RetrofitInstance.userService.getUser(userId)
                Log.d("ID", userId!!)
                Log.d("UserViewModel", "Response: $response")
                Log.d("UserViewModel", "ResponseBody: ${response.body()?.userProfileResponse}")

                if (response.isSuccessful) {
                    _uiState.value = UiState.FetchSuccess(
                        response.body()?.message ?: "Lấy thành công"
                    )
                    _user.value = response.body()?.userProfileResponse
                    delay(500)
                    _uiState.value = UiState.Idle
                    println("UI state value is idle")
                } else {
                    showError("Response get không hợp lệ")
                    _uiState.value = UiState.FetchFail(response.message())
                }
            } catch (e: IOException) {
                _uiState.value = UiState.FetchFail("Lỗi kết nối mạng: ${e.localizedMessage}")
                showError("Không thể kết nối máy chủ, vui lòng kiểm tra mạng.")
            } catch (e: Exception) {
                _uiState.value = UiState.FetchFail(e.message ?: "Lỗi hệ thống, vui lòng thử lại")
                showError("Lỗi mạng hoặc bất ngờ: ${e.localizedMessage ?: "Không rõ"}")
            }

        }
    }

    fun getUser(id: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.userService.getUser(id)
                Log.d("ID", id)
                Log.d("UserViewModel", "Response: $response")
                Log.d("UserViewModel", "ResponseBody: ${response.body()?.userProfileResponse}")

                if (response.isSuccessful) {
                    _user.value = response.body()?.userProfileResponse
                    println("UI state value is idle")
                } else {
                    showError("Response get không hợp lệ")
                }
            } catch (e: IOException) {
                showError("Không thể kết nối máy chủ, vui lòng kiểm tra mạng.")
            } catch (e: Exception) {
                showError("Lỗi mạng hoặc bất ngờ: ${e.localizedMessage ?: "Không rõ"}")
            }

        }
    }

    fun ModifierUser(userUpdateRequest: UserUpdateRequest, context: Context) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                Log.d("UserViewModel", "Request: $userUpdateRequest")
                Log.d("UserViewModel", "UserId: $userId")

                val avatar = userUpdateRequest.avatar?.let {
                    prepareFilePart(context, it, "avatar")
                }

                // Chỉ tạo MultipartBody.Part cho các trường không null
                val name = userUpdateRequest.name?.let {
                    MultipartBody.Part.createFormData("name", it)
                }
                val phone = userUpdateRequest.phone?.let {
                    MultipartBody.Part.createFormData("phone", it)
                }
                val email = userUpdateRequest.email?.let {
                    MultipartBody.Part.createFormData("email", it)
                }
                val username = userUpdateRequest.username?.let {
                    MultipartBody.Part.createFormData("username", it)
                }
                val introduce = userUpdateRequest.introduce?.let {
                    MultipartBody.Part.createFormData("introduce", it)
                }
                val skill = userUpdateRequest.skill?.let {
                    MultipartBody.Part.createFormData("skill", Gson().toJson(it))
                }
                val certificate = userUpdateRequest.certificate?.let {
                    MultipartBody.Part.createFormData("certificate", Gson().toJson(it))
                }

                val response = RetrofitInstance.userService.updateUser(
                    id = userId!!,
                    name = name,
                    phone = phone,
                    email = email,
                    username = username,
                    introduce = introduce,
                    skill = skill,
                    certificate = certificate,
                    avatar = avatar
                )
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("UserViewModel", "Success Response: ${responseBody?.message}")
                    _uiState.value = UiState.Success(
                        responseBody?.message ?: "Cập nhật thành công"
                    )
                    delay(500) // Cho Compose thời gian phản ứng trước khi đổi trạng thái
                    _uiState.value = UiState.Idle
                } else {
                    // Get error details from response body
                    val errorBody = response.errorBody()?.string()
                    Log.e("UserViewModel", "Error Response Body: $errorBody")

                    val errorMessage = when (response.code()) {
                        400 -> "Dữ liệu không hợp lệ: $errorBody"
                        404 -> "Không tìm thấy người dùng"
                        500 -> "Lỗi server: $errorBody"
                        else -> "Lỗi không xác định (${response.code()}): $errorBody"
                    }

                    showError(errorMessage)
                    _uiState.value = UiState.Error(errorMessage)
                }
            } catch (e: IOException) {
                val errorMsg = "Lỗi kết nối mạng: ${e.localizedMessage}"
                Log.e("UserViewModel", errorMsg, e)
                _uiState.value = UiState.Error(errorMsg)
                showError("Không thể kết nối máy chú, vui lòng kiểm tra mạng.")
            } catch (e: Exception) {
                val errorMsg = "Lỗi hệ thống: ${e.message ?: e.localizedMessage}"
                Log.e("UserViewModel", errorMsg, e)
                _uiState.value = UiState.Error(errorMsg)
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
            Log.e("UserViewModel", "Error preparing file part", e)
            null
        }
    }

    private fun showError(message: String) {
        _uiState.value = UiState.Error(message)
        Log.e("UserViewModel", message)
    }

    fun getUserFromFirestore() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                println("vao duoc day")
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid == null) {
                    showError("Không tìm thấy UID từ FirebaseAuth")
                    return@launch
                }

                val document = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .get()
                    .await()
                println("document duoc lay ra la"+ document.toString())

                if (document.exists()) {
                    val email = document.getString("email") ?: ""
                    val name = document.getString("displayName") ?: "Người dùng"
                    val photoUrl = document.getString("photoUrl") ?: ""
                    println("document ton tai")
                    _user.value = UserProfileResponse(
                        id = uid,
                        email = email,          // ✅ gán email từ Firestore
                        name = name,
                        avatar = photoUrl,
                        username = "",          // dùng giá trị mặc định nếu không có
                        phone = "",
                        introduce = "",
                        skill = emptyList(),
                        certificate = emptyList()
                    )

                    _uiState.value = UiState.FetchSuccess("Lấy thông tin từ Firestore thành công")
                } else {
                    showError("Không tìm thấy user trong Firestore")
                }

            } catch (e: Exception) {
                val msg = "Lỗi đọc Firestore: ${e.localizedMessage ?: "Không rõ"}"
                Log.e("UserViewModel", msg, e)
                _uiState.value = UiState.Error(msg)
            }
        }
    }


}
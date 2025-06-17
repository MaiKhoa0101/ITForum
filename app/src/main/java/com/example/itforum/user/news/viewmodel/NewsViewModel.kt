package com.example.itforum.user.news.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.service.NewsDao
import com.example.itforum.service.toEntity
import com.example.itforum.service.toModel
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.modelData.request.NewsRequest
import com.example.itforum.user.modelData.response.News
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import java.io.File

class NewsViewModel (
    private val newsDao: NewsDao,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _news = MutableStateFlow<News?>(null)
    val news: StateFlow<News?>get() = _news

    private val _listNews = MutableStateFlow<List<News>?>(null)
    val listNews: StateFlow<List<News>?>get() = _listNews

    private val _uiStateCreate = MutableStateFlow<UiState>(UiState.Idle)
    val uiStateCreate: StateFlow<UiState> = _uiStateCreate.asStateFlow()

    fun getNews() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = RetrofitInstance.newsService.getNews()

                if (response.isSuccessful) {
                    response.body()?.listNews?.let { newsList ->
                        val entities = newsList.map { it.toEntity() }
                        newsDao.clearAllNews()
                        newsDao.insertAll(entities)
                        _listNews.value = newsList
                    }
                    _uiState.value = UiState.FetchSuccess(
                        response.body()?.message ?: "Lấy tin tức thành công"
                    )
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
            } finally {
                newsDao.getAllNews().collect { cachedNews ->
                    _listNews.value = cachedNews.map { it.toModel() }
                }
                delay(500)
                _uiState.value = UiState.Idle
            }

        }
    }

    fun getByIdNews(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = RetrofitInstance.newsService.getNewsById(id)

                if (response.isSuccessful) {
                    response.body()?.let { news ->
                        val entity = news.toEntity()
                        newsDao.insertNews(entity)
                        _news.value  = news
                    }
                    _uiState.value = UiState.FetchSuccess("Lấy tin tức thành công")
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
            } finally {
                _news.value = newsDao.getNewsById(id)?.toModel()
                delay(500)
                _uiState.value = UiState.Idle
            }

        }
    }

    fun createNews(createNewsRequest: NewsRequest, context: Context) {
        viewModelScope.launch {
            _uiStateCreate.value = UiState.Loading
            try {
                Log.d("ComplaintViewModel", "Request: $createNewsRequest")

                val img = createNewsRequest.img?.let {
                    prepareFilePart(context, it, "img")
                }

                // Chỉ tạo MultipartBody.Part cho các trường không null
                val adminId = createNewsRequest.adminId.let {
                    MultipartBody.Part.createFormData("adminId", it)
                }
                val title = createNewsRequest.title.let {
                    MultipartBody.Part.createFormData("title", it)
                }
                val content = createNewsRequest.content.let {
                    MultipartBody.Part.createFormData("content", it)
                }

                val response = img?.let {
                    RetrofitInstance.newsService.createNews(
                        adminId = adminId,
                        title = title,
                        content = content,
                        img = it
                    )
                }
                img?.let {Log.d("Img", img.toString())}

                if (response != null) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("NewsViewModel", "Success Response: ${responseBody?.message}")
                        _uiStateCreate.value = UiState.Success(
                            responseBody?.message ?: "Tạo tin tức thành công"
                        )
                        delay(500) // Cho Compose thời gian phản ứng trước khi đổi trạng thái
                        _uiStateCreate.value = UiState.Idle
                    } else {
                        // Get error details from response body
                        val errorBody = response.errorBody()?.string()
                        Log.e("NewsViewModel", "Error Response Body: $errorBody")

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
            } catch (e: java.io.IOException) {
                val errorMsg = "Lỗi kết nối mạng: ${e.localizedMessage}"
                Log.e("NewsViewModel", errorMsg, e)
                _uiStateCreate.value = UiState.Error(errorMsg)
                showError("Không thể kết nối máy chú, vui lòng kiểm tra mạng.")
            } catch (e: Exception) {
                val errorMsg = "Lỗi hệ thống: ${e.message ?: e.localizedMessage}"
                Log.e("NewsViewModel", errorMsg, e)
                _uiStateCreate.value = UiState.Error(errorMsg)
                showError("Lỗi bất ngờ: ${e.localizedMessage ?: "Không rõ"}")
            }
        }
    }

    fun DeleteNews(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = RetrofitInstance.newsService.deleteNews(id)

                if (response.isSuccessful) {
                    newsDao.deleteNews(id)
                    _uiState.value = UiState.FetchSuccess("Xóa tin tức thành công")
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
            } finally {
                delay(500)
                _uiState.value = UiState.Idle
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
            Log.e("NewsViewModel", "Error preparing file part", e)
            null
        }
    }

    private fun showError(message: String) {
        _uiState.value = UiState.Error(message)
        Log.e("NewsViewModel", message)
    }
}
package com.example.itforum.user.news.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.modelData.response.News
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException

class NewsViewModel (private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _news = MutableStateFlow<News?>(null)
    val news: StateFlow<News?>get() = _news

    private val _listNews = MutableStateFlow<List<News>?>(null)
    val listNews: StateFlow<List<News>?>get() = _listNews

    fun getNews() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = RetrofitInstance.newsService.getNews()

                if (response.isSuccessful) {
                    _uiState.value = UiState.FetchSuccess(
                        response.body()?.message ?: "Lấy tin tức thành công"
                    )
                    _listNews.value  = response.body()?.listNews
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

    fun getByIdNews(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = RetrofitInstance.newsService.getNewsById(id)

                if (response.isSuccessful) {
                    _uiState.value = UiState.FetchSuccess("Lấy tin tức thành công")
                    _news.value  = response.body()
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
        Log.e("NewsViewModel", message)
    }
}
package com.example.itforum.user.home.tag.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.service.TagService
import com.example.itforum.user.modelData.response.TagItem
import com.example.itforum.user.modelData.request.TagRequest
import com.example.itforum.user.modelData.response.HideResponse
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response

class TagViewModel() : ViewModel() {

    private val _tagList = MutableStateFlow<List<TagItem>>(emptyList())
    val tagList: StateFlow<List<TagItem>> = _tagList

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _createResult = MutableStateFlow<HideResponse?>(null)
    val createResult: StateFlow<HideResponse?> = _createResult

    fun getAllTags() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response: Response<List<TagItem>> = RetrofitInstance.tagService.getAllTags()
                if (response.isSuccessful) {
                    _tagList.value = response.body().orEmpty()
                } else {
                    _error.value = "Error: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun searchTags(query: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = RetrofitInstance.tagService.searchTags(query)
                if (response.isSuccessful) {
                    _tagList.value = response.body().orEmpty()
                } else {
                    _error.value = "Error: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun createTag(name: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val request = TagRequest(name)
                val response = RetrofitInstance.tagService.createTag(request)
                if (response.isSuccessful) {
                    _createResult.value = response.body()
                    getAllTags()
                } else {
                    _error.value = "Create failed: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}

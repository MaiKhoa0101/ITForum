package com.example.itforum.user.userProfile.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.datastore.core.IOException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.service.toEntity
import com.example.itforum.service.toModel
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.modelData.response.Skill
import com.example.itforum.user.modelData.response.UserProfileResponse
import com.example.itforum.user.modelData.response.UserResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SkillViewModel : ViewModel() {
    private val _skill = MutableStateFlow<Skill?>(null)
    val skill: StateFlow<Skill?> get() = _skill

    private val _listSkill = MutableStateFlow<List<Skill>>(emptyList())
    val listSkill: StateFlow<List<Skill>> get() = _listSkill

    fun getAllSkill() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.skillService.getAllSkill()
                if (response.isSuccessful) {
                    _listSkill.value = response.body()!!.listSkill
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

    fun getByIdSkill(id: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.skillService.getSkillById(id)

                if (response.isSuccessful) {
                    response.body()?.let { skill ->
                        _skill.value  = skill
                    }
                }
                else {
                    showError("Response get không hợp lệ")
                }
            }
            catch (e: IOException) {
                showError("Không thể kết nối máy chủ, vui lòng kiểm tra mạng.")
            }
            catch (e: Exception) {
                showError("Lỗi mạng hoặc bất ngờ: ${e.localizedMessage ?: "Không rõ"}")
            }
        }
    }

    private fun showError(message: String) {
        Log.e("SkillViewModel", message)
    }
}
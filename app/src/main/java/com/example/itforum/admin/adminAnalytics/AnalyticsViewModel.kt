package com.example.itforum.admin.adminAnalytics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.service.AnalyticsApi
import com.example.itforum.user.Analytics.ScreenStat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AnalyticsViewModel : ViewModel() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://beitforum.onrender.com/") // 👈 Thay bằng link backend của bạn
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(AnalyticsApi::class.java)

    private val _stats = MutableStateFlow<List<ScreenStat>>(emptyList())
    val stats: StateFlow<List<ScreenStat>> = _stats

    init {
        viewModelScope.launch {
            try {
                val response = api.getScreenStats()
                _stats.value = response
            } catch (e: Exception) {
                Log.e("AnalyticsViewModel", "Error fetching stats: ${e.message}")
            }
        }
    }
}

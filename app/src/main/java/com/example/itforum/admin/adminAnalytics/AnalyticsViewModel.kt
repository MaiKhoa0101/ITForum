package com.example.itforum.admin.adminAnalytics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itforum.retrofit.RetrofitInstance

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnalyticsViewModel : ViewModel() {
    private val _events = MutableStateFlow<List<ScreenEvent>>(emptyList())
    val events: StateFlow<List<ScreenEvent>> = _events

    fun fetchEvents() {

        viewModelScope.launch {
            try {

                val result = RetrofitInstance.api.getScreenEvents()

                _events.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

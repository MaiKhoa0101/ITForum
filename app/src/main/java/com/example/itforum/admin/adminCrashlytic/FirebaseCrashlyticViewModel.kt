package com.example.itforum.admin.adminCrashlytic



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CrashLogViewModel : ViewModel() {
    private val _logs = MutableStateFlow<List<CrashLog>>(emptyList())
    val logs: StateFlow<List<CrashLog>> = _logs.asStateFlow()

    init {
        viewModelScope.launch {
            val crashList = FirebaseCrashlyticRepository.getAllCrashes()
            _logs.value = crashList
        }
    }
}

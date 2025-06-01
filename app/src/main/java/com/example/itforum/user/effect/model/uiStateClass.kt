package com.example.itforum.user.effect.model

sealed class UiState {
    object Loading : UiState()
    object Idle: UiState()
    data class Success(val message: String) : UiState()
    data class FetchSuccess(val message: String) : UiState()
    data class FetchFail(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}

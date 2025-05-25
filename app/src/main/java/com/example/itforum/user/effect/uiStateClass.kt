package com.example.itforum.user.effect

sealed class UiStateLogin {
    object Loading : UiStateLogin()
    object Idle: UiStateLogin()
    data class Success(val message: String) : UiStateLogin()
    data class Error(val message: String) : UiStateLogin()
}

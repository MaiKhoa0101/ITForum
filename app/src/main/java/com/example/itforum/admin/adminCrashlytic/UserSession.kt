package com.example.itforum.admin.adminCrashlytic


object UserSession {
    var email: String = "unknown"
    var userId: String = "unknown"

    fun load(email: String?, userId: String?) {
        this.email = email ?: "unknown"
        this.userId = userId ?: "unknown"
    }
}

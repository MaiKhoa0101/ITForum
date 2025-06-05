package com.example.itforum.user.model.request

import android.net.Uri
import com.example.itforum.user.model.response.Certificate
import com.example.itforum.user.model.response.Skill


data class UserUpdateRequest(
    val name: String?,
    val phone: String?,
    val email: String? ,
    val password: String? ,
    val username: String?,
    val introduce: String?,
    val skills: List<Skill>? = emptyList(),
    val certificate: List<Certificate>? = emptyList(),
    val avatar: Uri?,
)

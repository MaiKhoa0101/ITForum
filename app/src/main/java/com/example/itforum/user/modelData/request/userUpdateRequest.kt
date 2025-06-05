package com.example.itforum.user.modelData.request

import android.net.Uri
import com.example.itforum.user.modelData.response.Certificate
import com.example.itforum.user.modelData.response.Skill


data class UserUpdateRequest(
    val name: String?,
    val phone: String?,
    val email: String? ,
    val password: String? ,
    val username: String?,
    val introduce: String?,
    val skill: List<Skill>? = emptyList(),
    val certificate: List<Certificate>? = emptyList(),
    val avatar: Uri?,
)

package com.example.itforum.user.modelData.response

import com.example.itforum.admin.components.TableRowConvertible
import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("_id") val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val role: String,
    val username:String,
    val introduce: String,
    val avatar: String,
    val numberPost: Int,
    val numberComment: Int,
    val isBanned: Boolean,
    val certificate: List<Certificate>,
    val skill: List<String>
): TableRowConvertible {
    override fun toTableRow(): List<String> {
        return listOf(id, name, email, phone, if(isBanned) "Bị khóa" else "Hoạt động")
    }
}

data class SignOutResponse(
    val message: String
)
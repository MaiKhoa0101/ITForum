package com.example.itforum.user.modelData.response

import com.example.itforum.admin.components.TableRowConvertible
import com.google.gson.annotations.SerializedName

data class Complaint(
    @SerializedName("_id")
    val id: String,
    val userId: String,
    val title: String,
    val reason: String,
    val img: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int,
): TableRowConvertible {
    override fun toTableRow(): List<String> {
        return listOf(id, userId, title, reason, createdAt, status)
    }
}

data class CreateComplaintResponse(
    val message: String,
    val complaint: Complaint
)

data class GetComplaintResponse(
    val message: String,
    val listComplaint: List<Complaint>
)

data class HandleResponse(
    val message: String
)

data class DeleteResponse(
    val success: Boolean,
    val message: String
)
package com.example.itforum.user.modelData.response

import com.google.gson.annotations.SerializedName

data class ComplaintResponse(
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
)

data class CreateComplaintResponse(
    val message: String,
    val complaint: ComplaintResponse
)

data class GetComplaintResponse(
    val message: String,
    val listComplaint: List<ComplaintResponse>
)

data class HandleResponse(
    val message: String
)

data class DeleteResponse(
    val success: Boolean,
    val message: String
)
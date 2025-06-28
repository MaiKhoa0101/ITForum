package com.example.itforum.user.modelData.response

import com.google.gson.annotations.SerializedName


data class TagItem(
    @SerializedName("_id")
    val id: String,
    val name : String
)
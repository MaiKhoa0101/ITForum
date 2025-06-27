package com.example.itforum.user.modelData.response

import com.google.gson.annotations.SerializedName

data class Certificate(
    @SerializedName("_id") val id: String? = null,
    val name: String,
)

data class Skill(
    @SerializedName("_id") val id: String? = null,
    val name: String? = "",
    val totalUser: Int = 0
)

data class ListSkillResponse(
    val message: String,
    val listSkill: List<Skill>
)
package com.example.itforum.service

import com.example.itforum.user.modelData.response.ListSkillResponse
import com.example.itforum.user.modelData.response.Skill
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SkillService {
    @GET("skill/get/all")
    suspend fun getAllSkill(): Response<ListSkillResponse>

    @GET("skill/get/{id}")
    suspend fun getSkillById(@Path("id") id: String): Response<Skill>

}
package com.example.itforum.service
import com.example.itforum.user.modelData.request.TagRequest
import com.example.itforum.user.modelData.response.HideResponse
import com.example.itforum.user.modelData.response.TagItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface TagService {

    @GET("tags")
    suspend fun getAllTags(): Response<List<TagItem>>

    @GET("tags/search")
    suspend fun searchTags(@Query("query") query: String):Response<List<TagItem>>

    @POST("tags")
    suspend fun createTag(@Body request: TagRequest): Response<HideResponse>
}
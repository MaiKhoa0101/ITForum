package com.example.itforum.service

import com.example.itforum.user.modelData.request.ComplaintRequest
import com.example.itforum.user.modelData.response.ComplaintResponse
import com.example.itforum.user.modelData.response.CreateComplaintResponse
import com.example.itforum.user.modelData.response.DeleteResponse
import com.example.itforum.user.modelData.response.GetComplaintResponse
import com.example.itforum.user.modelData.response.HandleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ComplaintService {
    @GET("complaint/getall")
    suspend fun getComplaint(): Response<GetComplaintResponse>

    @GET("complaint/get/{id}")
    suspend fun getComplaintById(@Path("id") id: String): Response<ComplaintResponse>

    @GET("complaint/filter")
    suspend fun getComplaintByField(
        @Query("field") field: String,
        @Query("value") value: String
    ): Response<ComplaintResponse>

    @POST("complaint/create")
    suspend fun createComplaint(@Body complaint: ComplaintRequest): Response<CreateComplaintResponse>

    @PATCH("complaint/{id}/reject")
    suspend fun handleRejected(@Path("id") id: String): Response<HandleResponse>

    @PATCH("complaint/{id}/approve")
    suspend fun handleApproved(@Path("id") id: String): Response<HandleResponse>

    @DELETE("complaint/delete/{id}")
    suspend fun deleteComplaint(@Path("id") id: String): Response<DeleteResponse>
}
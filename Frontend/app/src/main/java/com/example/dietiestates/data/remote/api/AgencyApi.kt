package com.example.dietiestates.data.remote.api

import com.example.dietiestates.data.model.Agency
import com.example.dietiestates.data.model.Agent
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.dto.CreateAgentDto
import com.example.dietiestates.data.model.dto.CreateSupportAdminDto
import com.example.dietiestates.data.model.dto.ModifyOrCreateListingDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface AgencyApi {
    @GET("agency/agents")
    suspend fun getAgents(): Response<List<Agent>>

    @GET("agency")
    suspend fun getAgency(): Response<Agency>
    @POST("agency/agent")
    suspend fun createAgent(
        @Body dto: CreateAgentDto
    )

    @POST("agency/support-admin")
    suspend fun createSupportAdmin(
        @Body dto: CreateSupportAdminDto
    )
}

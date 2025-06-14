package com.example.dietiestates.data.remote.api

import com.example.dietiestates.data.model.Agency
import com.example.dietiestates.data.model.Agent
import com.example.dietiestates.data.model.dto.CreateAgentDto
import com.example.dietiestates.data.model.dto.CreateSupportAdminDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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

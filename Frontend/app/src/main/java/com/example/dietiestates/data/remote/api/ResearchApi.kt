package com.example.dietiestates.data.remote.api

import com.example.dietiestates.data.model.CreateResearchDto
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.Research
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ResearchApi {

    @GET("research")
    suspend fun getAllResearches(): Response<Research>

    @GET("research/last-TEN")
    suspend fun getLast10Researches(): Response<List<Research>>

    @POST("research")
    suspend fun createResearch(
        @Body researchDto: CreateResearchDto
    ): Response<List<Listing>>

    @PATCH("research/{id}")
    suspend fun updateResearch(
        @Path("id") researchId: String
    ): Response<Research>

    @DELETE("research/{id}")
    suspend fun deleteResearch(
        @Path("id") researchId: String
    ): Response<Unit>

}
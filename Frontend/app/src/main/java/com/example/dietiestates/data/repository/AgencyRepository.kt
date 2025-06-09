package com.example.dietiestates.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.dietiestates.data.model.Agency
import com.example.dietiestates.data.model.Agent
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.dto.CreateAgentDto
import com.example.dietiestates.data.model.dto.CreateSupportAdminDto
import com.example.dietiestates.data.model.dto.ModifyOrCreateListingDto
import com.example.dietiestates.data.remote.api.AgencyApi
import com.example.dietiestates.data.remote.api.ListingApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class AgencyRepository(private val api: AgencyApi) {

    suspend fun getAgents(): List<Agent> {
        val response = api.getAgents()

        if (response.isSuccessful ) {
            val agents = response.body() ?: emptyList()

            return agents
        } else {
            val code = response.code()
            throw Exception("Errore nel caricamento dei dati: HTTP $code")
        }
    }

    suspend fun getAgency(): Agency {
        val response = api.getAgency()

        if (response.isSuccessful ) {
            val agency = response.body() ?: Agency()

            return agency
        } else {
            val code = response.code()
            throw Exception("Errore nel caricamento dei dati: HTTP $code")
        }
    }

    suspend fun createAgent(dto: CreateAgentDto) {
        api.createAgent(dto)
    }

    suspend fun createSupportAdmin(dto: CreateSupportAdminDto) {
        api.createSupportAdmin(dto)
    }
}

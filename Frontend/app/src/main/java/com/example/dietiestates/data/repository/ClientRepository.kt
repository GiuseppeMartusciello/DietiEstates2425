package com.example.dietiestates.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.dietiestates.data.model.Agent
import com.example.dietiestates.data.model.Client
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.dto.ModifyOrCreateListingDto
import com.example.dietiestates.data.model.dto.UpdateNotificationPreferenceDto
import com.example.dietiestates.data.remote.api.AgencyApi
import com.example.dietiestates.data.remote.api.ClientApi
import com.example.dietiestates.data.remote.api.ListingApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ClientRepository(private val api: ClientApi) {

    suspend fun getMe(): Client {
        val response = api.getMe()

        if (response.isSuccessful ) {
            val client = response.body() ?: Client()

            return client
        } else {
            val code = response.code()
            throw Exception("Errore nel caricamento dei dati: HTTP $code")
        }
    }

    suspend fun updateNotification(type: String, value: Boolean) {
        val dto = UpdateNotificationPreferenceDto(type, value)
        val response = api.updateNotificationPreference(dto)

        if (!response.isSuccessful) {
            throw Exception("Errore aggiornamento preferenza. Riprova")
        }
    }
}

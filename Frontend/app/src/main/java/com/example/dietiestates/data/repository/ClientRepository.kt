package com.example.dietiestates.data.repository

import com.example.dietiestates.data.model.Client
import com.example.dietiestates.data.model.dto.UpdateNotificationPreferenceDto
import com.example.dietiestates.data.remote.api.ClientApi

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

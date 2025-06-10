package com.example.dietiestates.data.repository

import com.example.dietiestates.data.model.Notification
import com.example.dietiestates.data.model.dto.CreateNotificationDto
import com.example.dietiestates.data.remote.api.NotificationApi

class NotificationRepository(private val notificationApi: NotificationApi) {

    suspend fun createPromotionalNotification(
        createNotificationDto: CreateNotificationDto,
        listingId: String
    ) {
        val response = notificationApi.createPromotionalNotification(listingId,createNotificationDto)
        if (!response.isSuccessful) {
            throw Exception("Errore durante l'eliminazione: ${response.code()}")
        }
    }

    suspend fun notifications(): List<Notification> {
        val response = notificationApi.notifications()

        if(response.isSuccessful){
            val body = response.body() ?: emptyList()
            return body
        }
        else
        {
        val code = if (!response.isSuccessful) response.code() else response.code()
        throw Exception("Errore nel caricamento dei dati: HTTP $code")
        }
    }

    suspend fun getNotificationById(notificationId: String): Notification {

        val response = notificationApi.getNotificationById(notificationId)
        if(response.isSuccessful){
            val body = response.body() ?: throw Exception("No body in response")
            return body
        }
        else
        {
            val code = if (!response.isSuccessful) response.code() else response.code()
            throw Exception("Errore nel caricamento dei dati: HTTP $code")
        }
    }

    suspend fun updateNotification(userNotificationId: String) {
        val response = notificationApi.markNotificationAsRead(userNotificationId)

        if (!response.isSuccessful) {
            throw Exception("Errore durante l'eliminazione: ${response.code()}")
        }
    }

}
package com.example.dietiestates.data.remote.api

import com.example.dietiestates.data.model.Notification
import com.example.dietiestates.data.model.dto.CreateNotificationDto
import retrofit2.http.*
import retrofit2.Response

interface NotificationApi {

    //  Restituisce le notifiche non lette per l’utente autenticato
    @GET("notification/Notifications")
    suspend fun notifications(): Response<List<Notification>>

    // Aggiorna una notifica a letta
    @PATCH("notification/{notificationId}")
    suspend fun markNotificationAsRead(
        @Path("notificationId") userNotificationId: String
    ): Response<Void>

}

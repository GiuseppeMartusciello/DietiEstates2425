package com.example.dietiestates.data.remote.api

import com.example.dietiestates.data.model.Notification
import com.example.dietiestates.data.model.dto.CreateNotificationDto
import retrofit2.http.*
import retrofit2.Response

interface NotificationApi {

    // Crea una notifica promozionale
    @POST("notification/listing/{listingId}")
    suspend fun createPromotionalNotification(
        @Path("listingId") listingId: String,
        @Body body: CreateNotificationDto
    ): Response<Notification>

    //  Restituisce le notifiche non lette per l’utente autenticato
    @GET("notification/Notifications")
    suspend fun notifications(): Response<List<Notification>>

    // Restituisce una notifica specifica
    @GET("notification/{notificationId}")
    suspend fun getNotificationById(
        @Path("notificationId") notificationId: String
    ): Response<Notification>

    // Aggiorna una notifica a letta
    @PATCH("notification/{notificationId}")
    suspend fun markNotificationAsRead(
        @Path("notificationId") userNotificationId: String
    ): Response<Void>


   /* @POST("notification/test-push")
    suspend fun sendTestPush(
        @Body body: FcmTestBody
    ): Response<Void>*/
}

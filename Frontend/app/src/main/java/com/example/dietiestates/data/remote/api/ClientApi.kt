package com.example.dietiestates.data.remote.api

import com.example.dietiestates.data.model.Client
import com.example.dietiestates.data.model.dto.UpdateNotificationPreferenceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface ClientApi {
    @GET("client/me")
    suspend fun getMe(): Response<Client>

    @PATCH("client/notification-preference")
    suspend fun updateNotificationPreference(
        @Body dto: UpdateNotificationPreferenceDto
    ): Response<Unit>

}
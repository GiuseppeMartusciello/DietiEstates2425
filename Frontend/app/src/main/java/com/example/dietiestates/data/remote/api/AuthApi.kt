package com.example.dietiestates.data.remote.api

import com.example.dietiestates.data.model.LoginRequest
import com.example.dietiestates.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/signin")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}

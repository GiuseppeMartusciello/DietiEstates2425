package com.example.dietiestates.data.remote.api

import com.example.dietiestates.data.model.ChangePasswordRequest
import com.example.dietiestates.data.model.ChangePasswordResponse
import com.example.dietiestates.data.model.LoginRequest
import com.example.dietiestates.data.model.LoginResponse
import com.example.dietiestates.data.model.SignUpRequest
import com.example.dietiestates.data.model.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/signin")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/google-token")
    suspend fun loginWithGoogleToken(@Body body: Map<String, String>): LoginResponse

    @POST("auth/signup")
    suspend fun  signUp (@Body request: SignUpRequest): Response<SignUpResponse>

    @PATCH("user/change-password")
    suspend fun changePassword (@Body request: ChangePasswordRequest): Response<ChangePasswordResponse>
}

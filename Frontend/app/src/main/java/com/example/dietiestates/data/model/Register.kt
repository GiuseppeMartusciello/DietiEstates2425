package com.example.dietiestates.data.model

data class SignUpRequest(
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
    val phone: String
)

data class SignUpResponse(
    val accessToken: String,
    val mustChangePassword: Boolean
)

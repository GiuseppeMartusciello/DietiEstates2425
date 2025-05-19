package com.example.dietiestates.data.model

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,

)

data class ChangePasswordResponse(
    val message: String
)

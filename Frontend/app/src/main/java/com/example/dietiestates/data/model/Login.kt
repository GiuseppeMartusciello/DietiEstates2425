package com.example.dietiestates.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val accessToken: String,
    val mustChangePassword: Boolean
)

data class AuthResult(
    val success: Boolean,
    val mustChangePassword: Boolean = false,
    val errorMessage: String? = null
)

enum class PostLoginNavigation {
    HOME,
    CHANGE_PASSWORD,

}

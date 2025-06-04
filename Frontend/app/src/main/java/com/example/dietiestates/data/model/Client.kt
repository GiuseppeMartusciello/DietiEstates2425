package com.example.dietiestates.data.model

data class Client (
    val id: String = "",
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val phone: String = "",
    val address: String = "",
    val promotionalNotification: Boolean = false,
    val offerNotification: Boolean = false,
    val searchNotification: Boolean = false
)
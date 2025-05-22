package com.example.dietiestates.data.model

data class PropertyOffer(
    val id: String,
    val price: Double,
    val date: String,
    val state: String,
    val madeByUser: Boolean,
    val guestEmail: String?,
    val guestName: String?
)



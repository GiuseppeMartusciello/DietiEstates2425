package com.example.dietiestates.data.model

import java.util.Date

data class PropertyOffer(
    val id: String,
    val price: Double,
    val date: String,
    val state: String,
    val madeByUser: Boolean,
    val guestEmail: String?,
    val guestName: String?,
    val guestSurname: String?
)

data class CreateOfferDto(
    val price: Double
)

data class CreateExternalOfferDto(
    val guestEmail: String,
    val guestName: String,
    val guestSurname: String,
    val price: Double
)

data class OfferStatusDto(
    val status: String
)

data class ClientsOffer (
    val userId: String,
    val name: String,
    val surname: String,
    val email: String,
    val phone: String,
    val lastOffer: LastOfferDto
)


data class LastOfferDto (
    val id: String,
    val price: Double,
    val date: String,
    val state: String,
    val madeByUser: Boolean
)

data class Guest (
    val guestName: String,
    val guestSurname: String,
    val guestEmail: String
)


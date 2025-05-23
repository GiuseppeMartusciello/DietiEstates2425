package com.example.dietiestates.data.model.dto

data class ListingFormState(
    val address: String = "",
    val title: String = "",
    val municipality: String = "",
    val postalCode: String = "",
    val province: String = "",
    val size: String = "",
    val numberOfRooms: String = "0",
    val energyClass: String = "A",
    val description: String = "",
    val price: String = "",
    val category: String = "Vendita",
    val floor: String = "0",
    val hasElevator: Boolean = false,
    val hasAirConditioning: Boolean = false,
    val hasGarage: Boolean = false
)

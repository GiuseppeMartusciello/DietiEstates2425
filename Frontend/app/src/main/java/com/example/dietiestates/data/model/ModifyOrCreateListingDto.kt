package com.example.dietiestates.data.model

data class ModifyOrCreateListingDto(
    val address: String,
    val title: String,
    val municipality: String,
    val postalCode: String,
    val province: String,
    val size: String,
    val numberOfRooms: Int,
    val energyClass: String,
    val description: String,
    val price: Long,
    val category: String,
    val floor: String,
    val hasElevator: Boolean,
    val hasAirConditioning: Boolean,
    val hasGarage: Boolean
)


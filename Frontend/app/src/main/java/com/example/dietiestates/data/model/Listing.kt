package com.example.dietiestates.data.model

data class Listing(
    val id: String,
    val title: String,
    val address: String,
    val municipality: String,
    val postalCode: String,
    val province: String,
    val size: String,
    val latitude: Double,
    val longitude: Double,
    val numberOfRooms: String,
    val energyClass: Char,
    val nearbyPlaces: ArrayList<String>,
    val description: String,
    val price: Long,
    val category: String,
    val floor: String,
    val hasElevator: Boolean,
    val hasAirConditioning: Boolean,
    val hasGarage: Boolean,
    val imageUrls: List<String> = emptyList()
)



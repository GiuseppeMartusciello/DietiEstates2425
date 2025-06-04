package com.example.dietiestates.data.model

data class Research(
    val id: String,
    val searchType: String,
    val date: String,

    val municipality: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radius: Double? = null,

    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val numberOfRooms: Int? = null,

    val category: String? = null,
    val minSize: String? = null,
    val energyClass: String? = null,

    val hasElevator: Boolean? = null,
    val hasAirConditioning: Boolean? = null,
    val hasGarage: Boolean? = null
)




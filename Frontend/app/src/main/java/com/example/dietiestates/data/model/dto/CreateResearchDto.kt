package com.example.dietiestates.data.model.dto

data class CreateResearchDto(
val searchType: String,
val municipality: String?,
val latitude: Double?,
val longitude: Double?,
val radius: Double?,
val minPrice: Int?,
val maxPrice: Int?,
val numberOfRooms: Int?,
val category: String?,
val minSize: Int?,
val energyClass: String?,
val hasElevator: Boolean?,
val hasAirConditioning: Boolean?,
val hasGarage: Boolean?
)

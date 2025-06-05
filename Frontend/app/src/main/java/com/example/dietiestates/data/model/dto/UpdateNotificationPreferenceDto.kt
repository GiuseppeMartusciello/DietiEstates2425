package com.example.dietiestates.data.model.dto

data class UpdateNotificationPreferenceDto(
    val type: String,  // "promotional", "offer", "search"
    val value: Boolean
)

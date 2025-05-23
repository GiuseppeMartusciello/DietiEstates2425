package com.example.dietiestates.data.model

import java.util.Date

data class Agent(
    val userId: String,
    val licenseNumber: String,
    val start_date: Date,
    val languages: List<String> = emptyList()
)

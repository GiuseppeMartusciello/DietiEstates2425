package com.example.dietiestates.data.model

import java.util.Date

data class Agent(
    val name: String,
    val surname: String,
    val userId: String,
    val licenseNumber: String,
    val birthDate: Date,
    val start_date: Date,
    val languages: List<String> = emptyList(),
    val agencyName: String,
    val agencyAddress: String,
)


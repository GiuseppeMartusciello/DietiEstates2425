package com.example.dietiestates.data.model.dto

data class CreateAgentDto(
    val licenseNumber: String,
    val name: String,
    val surname: String,
    val email: String,
    val birthDate: String,
    val gender: String,
    val phone: String,
    val start_date: String,
    val languages: List<String>
)

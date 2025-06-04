package com.example.dietiestates.data.model

import java.util.Date

data class Notification(
    val id: String,
    val category: String,
    val title: String,
    val description: String,
    val date: Date,
    val listing: Listing?,                // può essere null → nullable
    val propertyOffer: PropertyOffer?,    // può essere null → nullable
    val userNotifications: List<UserNotification>,
)

package com.example.dietiestates.data.model

data class UserNotification(
    val id: String,
    val notification: Notification,
    val user: String,
    val isRead: Boolean = false
)
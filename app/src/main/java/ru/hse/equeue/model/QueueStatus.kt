package ru.hse.equeue.model

data class QueueStatus(
    val currentUsersCount: Double,
    val totalUsersCount: Double,
    val serviceTime: Double,
    val status: String
)
package ru.hse.equeue.model

data class QueueStatus(
    val currentUsersCount: Int,
    val totalUsersCount: Int,
    val serviceTime: Double,
    val status: String
)
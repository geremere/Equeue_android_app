package ru.hse.equeue.model

data class Queue(
    val id: Long,
    val photoUrl: String,
    val name: String,
    val x: Double,
    val y: Double,
    val ownerId: String,
    val address: String,
    val status: QueueStatus,
    val usersQueue: List<UserInQueue>
)
package ru.hse.equeue.model

data class Queue(
    val id: Long,
    val photoUrl: String,
    val name: String,
    val x: Double,
    val y: Double,
    val ownerId: String,
    val averageWaitingTime: Double,
    val address: String,
    val countOfPeople: Int,
    val status: QueueStatus
)
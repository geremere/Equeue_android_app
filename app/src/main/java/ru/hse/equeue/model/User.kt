package ru.hse.equeue.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String,
    val queue: Queue
)
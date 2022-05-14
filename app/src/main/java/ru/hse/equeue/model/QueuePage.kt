package ru.hse.equeue.model

data class QueuePage(
    val totalPages: Int,
    val totalElements: Int,
    val size: Int,
    val content: List<Queue>,
    val number: Int,
    val empty: Boolean
)
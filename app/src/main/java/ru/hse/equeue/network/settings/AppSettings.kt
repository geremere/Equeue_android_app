package ru.hse.equeue.network.settings

interface AppSettings {

    fun getCurrentToken(): String?

    fun setCurrentToken(token: String?)

}
package ru.hse.equeue.network.base

import ru.hse.equeue.network.auth.AuthService
import ru.hse.equeue.network.queue.AddressesService
import ru.hse.equeue.network.queue.QueueService
import ru.hse.equeue.network.user.UserService

class OkHttpProvider(
    private val config: OkHttpConfig
) {
    fun getAuthService(): AuthService {
        return AuthService(config)
    }

    fun getUserService(): UserService {
        return UserService(config)
    }

    fun getAddressesService(): AddressesService {
        val yandexConfig = OkHttpConfig(
            baseUrl = "https://search-maps.yandex.ru/v1/?apikey=f8ed00e8-a639-44ba-b57f-98fbfa76be49",
            gson = config.gson,
            client = config.client
        )
        return AddressesService(yandexConfig)
    }

    fun getQueueService(): QueueService {
        return QueueService(config)
    }
}
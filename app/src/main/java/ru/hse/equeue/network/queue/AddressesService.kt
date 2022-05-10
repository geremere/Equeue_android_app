package ru.hse.equeue.network.queue

import okhttp3.Request
import ru.hse.equeue.model.User
import ru.hse.equeue.network.base.BaseOkHttpService
import ru.hse.equeue.network.base.OkHttpConfig

class AddressesService(
    config: OkHttpConfig
) : BaseOkHttpService(config) {

    suspend fun getAddresses(text:String): User {
        val request = Request.Builder()
            .get()
            .endpoint("&text=${text}&lang=ru_RU")
            .build()
        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse()
    }
}
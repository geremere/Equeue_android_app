package ru.hse.equeue.network.queue

import okhttp3.Request
import ru.hse.equeue.network.base.BaseOkHttpService
import ru.hse.equeue.network.base.OkHttpConfig
import ru.hse.equeue.network.base.Result

class AddressesService(
    config: OkHttpConfig
) : BaseOkHttpService(config) {

    suspend fun getAddresses(text: String): Result<List<String>> {
        val request = Request.Builder()
            .get()
            .endpoint("&text=${text}&lang=ru_RU")
            .build()
        return client.newCall(request).suspendEnqueue<List<String>>()
    }
}
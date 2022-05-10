package ru.hse.equeue.network.user

import okhttp3.Request
import ru.hse.equeue.model.User
import ru.hse.equeue.network.base.BaseOkHttpService
import ru.hse.equeue.network.base.OkHttpConfig

class UserService(
    config: OkHttpConfig
) : BaseOkHttpService(config) {

    suspend fun getUser(): User {
        val request = Request.Builder()
            .get()
            .endpoint("/user/get")
            .build()
        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse<User>()
    }
}
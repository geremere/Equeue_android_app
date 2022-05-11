package ru.hse.equeue.network.user

import okhttp3.Request
import ru.hse.equeue.model.User
import ru.hse.equeue.network.base.BaseOkHttpService
import ru.hse.equeue.network.base.OkHttpConfig
import ru.hse.equeue.network.base.Result

class UserService(
    config: OkHttpConfig
) : BaseOkHttpService(config) {

    suspend fun getUser(): Result<User> {
        val request = Request.Builder()
            .get()
            .endpoint("/user/get")
            .build()
        return client.newCall(request).suspendEnqueue()
    }

}
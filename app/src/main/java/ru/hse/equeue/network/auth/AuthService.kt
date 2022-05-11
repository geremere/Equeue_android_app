package ru.hse.equeue.network.auth

import okhttp3.Request
import ru.hse.equeue.model.User
import ru.hse.equeue.network.base.BaseOkHttpService
import ru.hse.equeue.network.base.OkHttpConfig
import ru.hse.equeue.network.base.Result

class AuthService(
    config: OkHttpConfig
) : BaseOkHttpService(config) {
    suspend fun signIn(googleToken: String): Result<String> {
        val request = Request.Builder()
            .get()
            .endpoint("/auth/" + googleToken)
            .build()
        return client.newCall(request).suspendEnqueue()
    }

    suspend fun getUser(jwtToken: String): Result<User> {
        val request = Request.Builder()
            .get()
            .endpoint("/user")
            .build()
        return client.newCall(request).suspendEnqueue()
    }
}
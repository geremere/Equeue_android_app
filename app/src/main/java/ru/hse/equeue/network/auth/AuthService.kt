package ru.hse.equeue.network.auth

import okhttp3.Request
import ru.hse.equeue.model.User
import ru.hse.equeue.network.base.BaseOkHttpService
import ru.hse.equeue.network.base.OkHttpConfig

class AuthService(
    config: OkHttpConfig
) : BaseOkHttpService(config) {
    suspend fun signIn(googleToken: String):String {
        val request = Request.Builder()
            .get()
            .endpoint("/auth/" + googleToken)
            .build()
        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse<String>()
    }

    suspend fun getUser(jwtToken: String):User {
        val request = Request.Builder()
            .get()
            .endpoint("/user")
            .build()
        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse<User>()
    }
}
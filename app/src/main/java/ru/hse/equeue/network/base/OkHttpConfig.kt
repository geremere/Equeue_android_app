package ru.hse.equeue.network.base

import com.google.gson.Gson
import okhttp3.OkHttpClient

class OkHttpConfig(
    val baseUrl: String,
    val client: OkHttpClient,
    val gson: Gson
)
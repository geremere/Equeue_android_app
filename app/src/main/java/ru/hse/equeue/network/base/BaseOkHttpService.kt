package ru.hse.equeue.network.base

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.ConnectException
import kotlin.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class BaseOkHttpService(
    private val config: OkHttpConfig
) {
    val gson: Gson = config.gson
    val client: OkHttpClient = config.client
    private val contentType = "application/json; charset=utf-8".toMediaType()

    suspend fun Call.suspendEnqueue(): Response {
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation {
                cancel()
            }

            enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.message?.let { Log.i("ERROR_EQUEUE", it) }
                    continuation.resumeWithException(ConnectException(e.message))
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        continuation.resume(response)
                    } else {
                        handleErrorResponse(response, continuation)
                    }
                }

            })
        }
    }

    private fun handleErrorResponse(
        response: Response,
        continuation: CancellableContinuation<Response>
    ) {
        val httpCode = response.code
        try {
            val map = gson.fromJson(response.body!!.string(), Map::class.java)
            val message = map["error"].toString()
            continuation.resumeWithException(ConnectException(message))
        } catch (e: Exception) {
            continuation.resumeWithException(ConnectException(e.message))
        }
    }

    fun Request.Builder.endpoint(endpoint: String): Request.Builder {
        url("${config.baseUrl}$endpoint")
        return this
    }

    fun <T> T.toJsonRequestBody(): RequestBody {
        val json = gson.toJson(this)
        return json.toRequestBody(contentType)
    }

    fun <T> Response.parseJsonResponse(typeToken: TypeToken<T>): T {
        try {
            return gson.fromJson(this.body!!.string(), typeToken.type)
        } catch (e: Exception) {
            throw ConnectException(e.message)
        }

    }

    inline fun <reified T> Response.parseJsonResponse(): T {
        try {
            return gson.fromJson(this.body!!.string(), T::class.java)
        } catch (e: Exception) {
            throw ConnectException(e.message)
        }
    }
}
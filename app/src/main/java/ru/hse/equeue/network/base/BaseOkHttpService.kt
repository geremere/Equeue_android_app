package ru.hse.equeue.network.base

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.hse.equeue.model.ErrorResponse
import java.io.IOException
import java.net.ConnectException
import kotlin.coroutines.resume

open class BaseOkHttpService(
    private val config: OkHttpConfig
) {
    val gson: Gson = config.gson
    val client: OkHttpClient = config.client
    private val contentType = "application/json; charset=utf-8".toMediaType()

    suspend inline fun <reified T> Call.suspendEnqueue(): Result<T> {
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation {
                cancel()
            }

            enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.message?.let { Log.i("ERROR_EQUEUE", it) }
                    continuation.resume(Result.Failure(true, null, e.message))
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val result =  response.body?.string()
                        try {
                            val type = object : TypeToken<T>() {}.type
                            continuation.resume(
                                Result.Success<T>(
                                    gson.fromJson(
                                        result,
                                        type
                                    )
                                )
                            )
//                            continuation.resume(
//                                Result.Success<T>(
//                                    gson.fromJson(
//                                       result,
//                                        T::class.java
//                                    )
//                                )
//                            )
                        } catch (e: UnsupportedOperationException) {
                            val type = object : TypeToken<T>() {}.type
                            continuation.resume(
                                Result.Success<T>(
                                    gson.fromJson(
                                        result,
                                        type
                                    )
                                )
                            )
                        }


                    } else {
                        continuation.resume(
                            Result.Failure(
                                false,
                                response.code,
                                response.parseErrorResponse()
                            )
                        )
                    }
                }

            })
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

    inline fun Response.parseErrorResponse(): String {
        try {
            return gson.fromJson(this.body!!.string(), ErrorResponse::class.java).message
        } catch (e: Exception) {
            throw ConnectException(e.message)
        }
    }
}
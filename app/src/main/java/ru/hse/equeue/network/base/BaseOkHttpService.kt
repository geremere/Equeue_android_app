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

sealed class Result<out T> {

    data class Success<out T>(val value: T) : Result<T>()

    val gson: Gson = Gson()

    data class Failure(
        val isNetworkError: Boolean,
        val code: Int?,
        val message: String?
    ) : Result<Nothing>()

    inline fun onSuccess(execute: (obj: T) -> Unit): Result<T> {
        if (this is Success<T>) {
            execute(value)
        }
        return this
    }

    inline fun onFailure(execute: (res: Failure) -> Unit): Result<T> {
        if (this is Failure) {
            execute(this)
        }
        return this
    }
}

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
                        continuation.resume(Result.Success<T>(gson.fromJson(response.body!!.string(), T::class.java)))
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

//    fun <T> Response.parseJsonResponse(typeToken: TypeToken<T>): T {
//        try {
//            return gson.fromJson(this.body!!.string(), typeToken.type)
//        } catch (e: Exception) {
//            throw ConnectException(e.message)
//        }
//
//    }

    inline fun Response.parseErrorResponse(): String {
        try {
            return gson.fromJson(this.body!!.string(), ErrorResponse::class.java).message
        } catch (e: Exception) {
            throw ConnectException(e.message)
        }
    }
}
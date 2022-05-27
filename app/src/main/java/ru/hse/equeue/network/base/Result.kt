package ru.hse.equeue.network.base

import com.google.gson.Gson

sealed class Result<out T> {

    protected var hasBeenHandled: Boolean = false

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
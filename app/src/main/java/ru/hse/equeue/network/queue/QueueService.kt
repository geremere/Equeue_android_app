package ru.hse.equeue.network.queue

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import ru.hse.equeue.model.CreateQueue
import ru.hse.equeue.model.ListQueueRequest
import ru.hse.equeue.model.Queue
import ru.hse.equeue.model.QueuePage
import ru.hse.equeue.network.base.BaseOkHttpService
import ru.hse.equeue.network.base.OkHttpConfig
import ru.hse.equeue.network.base.Result
import java.io.File

class QueueService(
    config: OkHttpConfig
) : BaseOkHttpService(config) {

    suspend fun createQueue(queue: CreateQueue, file: File): Result<Queue> {
        val body: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                file.name,
                RequestBody.create("image/*".toMediaTypeOrNull(), file)
            )
            .addFormDataPart("queue", null, queue.toJsonRequestBody())
            .build()
        val request = Request.Builder()
            .post(body)
            .endpoint("/queue")
            .build()

        return client.newCall(request).suspendEnqueue()
    }

    suspend fun getQueueByUserId(userId: String): Result<Queue> {
        val request = Request.Builder()
            .get()
            .endpoint("/queue/byUserId")
            .build()
        return client.newCall(request).suspendEnqueue()
    }

    suspend fun getListQueueByPage(
        request: ListQueueRequest,
        page: Int,
        size: Int
    ): Result<QueuePage> {
        val request = Request.Builder()
            .post(request.toJsonRequestBody())
            .endpoint(
                "/queue/list/page" + getRequestParams(
                    mapOf(
                        "page" to page.toString(),
                        "size" to size.toString()
                    )
                )
            )
            .build()
        return client.newCall(request).suspendEnqueue()
    }

    suspend fun searchQueueByPage(
        request: ListQueueRequest,
        size: Int,
        search:String
    ): Result<QueuePage> {
        val request = Request.Builder()
            .post(request.toJsonRequestBody())
            .endpoint(
                "/queue/list/page" + getRequestParams(
                    mapOf(
                        "page" to "0",
                        "size" to size.toString(),
                        "search" to search
                    )
                )
            )
            .build()
        return client.newCall(request).suspendEnqueue()
    }

    suspend fun getListQueue(request: ListQueueRequest): Result<List<Queue>> {
        val request = Request.Builder()
            .post(request.toJsonRequestBody())
            .endpoint("/queue/list")
            .build()
        return client.newCall(request).suspendEnqueue()
    }

    suspend fun standToQueue(queueId: Long): Result<Queue> {
        val request = Request.Builder()
            .get()
            .endpoint("/queue/stand" + getRequestParams(mapOf("queueId" to queueId.toString())))
            .build()
        return client.newCall(request).suspendEnqueue()
    }

    suspend fun outFromQueue(queueId: Long): Result<Void> {
        val request = Request.Builder()
            .delete()
            .endpoint("/queue/remove" + getRequestParams(mapOf("queueId" to queueId.toString())))
            .build()
        return client.newCall(request).suspendEnqueue()
    }

    suspend fun serveUser(userId: String?): Result<Queue> {
        var endpoint = "/queue/serve"
        if (userId != null) {
            endpoint += getRequestParams(mapOf("userId" to userId))
        }
        val request = Request.Builder()
            .get()
            .endpoint(endpoint)
            .build()
        return client.newCall(request).suspendEnqueue()
    }

    suspend fun changeStatus(status: String): Result<Queue> {
        val request = Request.Builder()
            .get()
            .endpoint("/queue/status" + getRequestParams(mapOf("status" to status)))
            .build()
        return client.newCall(request).suspendEnqueue()
    }

    fun getRequestParams(params: Map<String, String>): String {
        val paramsString = StringBuilder("?")
        params.keys.forEach { key ->
            paramsString.append(key + "=" + params.get(key) + "&")
        }
        paramsString.setLength(paramsString.length - 1)
        return paramsString.toString()
    }
}
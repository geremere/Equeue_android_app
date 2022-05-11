package ru.hse.equeue.network.queue

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import ru.hse.equeue.model.CreateQueue
import ru.hse.equeue.model.Queue
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
}
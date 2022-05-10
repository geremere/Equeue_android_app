package ru.hse.equeue.network.queue

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import ru.hse.equeue.model.CreateQueue
import ru.hse.equeue.model.Queue
import ru.hse.equeue.network.base.BaseOkHttpService
import ru.hse.equeue.network.base.OkHttpConfig
import java.io.File

class QueueService(
    config: OkHttpConfig
) : BaseOkHttpService(config) {

    suspend fun createQueue(queue: CreateQueue, file: File): Queue {
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

        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse<Queue>()
    }

    suspend fun getQueueByUserId(userId:String):Queue{
        val request = Request.Builder()
            .get()
            .endpoint("/queue/byUserId")
            .build()
        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse()
    }
}
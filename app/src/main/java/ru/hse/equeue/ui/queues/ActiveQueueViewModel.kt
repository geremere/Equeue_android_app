package ru.hse.equeue.ui.queues

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.equeue.model.Queue
import ru.hse.equeue.model.QueueStatus
import ru.hse.equeue.network.queue.QueueService
import ru.hse.equeue.network.settings.Singletons
import java.lang.Exception

class ActiveQueueViewModel(application: Application) : AndroidViewModel(application) {

    private val queueService: QueueService = Singletons.queueService

    private val _queue = MutableLiveData<Queue>()

    val queue: LiveData<Queue> = _queue

    fun setQueue(queue: Queue) {
        _queue.value = queue
    }

    fun getActiveQueue(userId: String) {
        viewModelScope.launch {
            try {
                val queue: Queue = queueService.getQueueByUserId(userId)
                _queue.value = queue
            } catch (e: Exception) {
                Log.i("EXCEPTION_EQUEUE", e.message.toString())
            }
        }
    }
}
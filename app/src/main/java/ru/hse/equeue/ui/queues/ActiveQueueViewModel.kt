package ru.hse.equeue.ui.queues

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.equeue.model.Queue
import ru.hse.equeue.network.base.Result
import ru.hse.equeue.network.queue.QueueService
import ru.hse.equeue.network.settings.Singletons

class ActiveQueueViewModel(application: Application) : AndroidViewModel(application) {

    private val queueService: QueueService = Singletons.queueService

    private val _standToQueueResult = MutableLiveData<Result<Queue>>()
    var outFromQueueResult = MutableLiveData<Result<Void>>()
    private val _queue = MutableLiveData<Queue>()
    var selectedQueue = MutableLiveData<Queue>()

    val queue: LiveData<Queue> = _queue
    val queueResult: LiveData<Result<Queue>> = _standToQueueResult

    fun setQueue(queue: Queue) {
        _queue.value = queue
    }

    fun getActiveQueue(userId: String) {
        viewModelScope.launch {
            _standToQueueResult.value = queueService.getQueueByUserId(userId)
        }
    }

    fun standToQueue(queueId: Long) {
        viewModelScope.launch {
            _standToQueueResult.value = queueService.standToQueue(queueId)
        }
    }

    fun outFromQueue() {
        viewModelScope.launch {
            outFromQueueResult.value = queueService.outFromQueue(_queue.value!!.id)
        }

    }
}
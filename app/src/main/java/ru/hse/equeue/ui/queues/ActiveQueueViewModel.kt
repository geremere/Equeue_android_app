package ru.hse.equeue.ui.queues

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.equeue.model.Event
import ru.hse.equeue.model.Queue
import ru.hse.equeue.network.base.Result
import ru.hse.equeue.network.queue.QueueService
import ru.hse.equeue.network.settings.Singletons

class ActiveQueueViewModel(application: Application) : AndroidViewModel(application) {

    private val queueService: QueueService = Singletons.queueService

    var queueResult = MutableLiveData<Event<Result<Queue>>>()
    var standToQueueResult = MutableLiveData<Event<Result<Queue>>>()
    var outFromQueueResult = MutableLiveData<Event<Result<Void>>>()
    var queue = MutableLiveData<Queue>()
    var selectedQueue = MutableLiveData<Queue>()


    fun getActiveQueue(userId: String) {
        viewModelScope.launch {
            queueResult.value = Event(queueService.getQueueByUserId(userId))
        }
    }


    fun standToQueue(queueId: Long) {
        viewModelScope.launch {
            standToQueueResult.value = Event(queueService.standToQueue(queueId))
        }
    }

    fun outFromQueue() {
        viewModelScope.launch {
            outFromQueueResult.value = Event(queueService.outFromQueue(queue.value!!.id))
        }

    }
}
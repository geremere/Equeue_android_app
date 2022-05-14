package ru.hse.equeue.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.equeue.model.ListQueueRequest
import ru.hse.equeue.model.Queue
import ru.hse.equeue.network.base.Result
import ru.hse.equeue.network.queue.QueueService
import ru.hse.equeue.network.settings.Singletons


class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val queueService: QueueService = Singletons.queueService

    var currentLocation = MutableLiveData<ListQueueRequest>()

    private var _queues = mutableListOf<Queue>()
    var queuesResult = MutableLiveData<Result<List<Queue>>>()

    val queue: MutableList<Queue> = _queues

    fun getQueues() {
        viewModelScope.launch {
            val result = queueService.getListQueue(currentLocation.value!!)
            queuesResult.value = result
        }
    }

    fun setQueues(newQueues: List<Queue>) {
        for (i in _queues.indices) {
            _queues.removeAt(i)
        }
        _queues.addAll(newQueues)
    }
}
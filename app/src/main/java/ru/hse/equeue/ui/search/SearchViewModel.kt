package ru.hse.equeue.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import kotlinx.coroutines.launch
import ru.hse.equeue.model.ListQueueRequest
import ru.hse.equeue.model.Queue
import ru.hse.equeue.model.QueuePage
import ru.hse.equeue.network.base.Result
import ru.hse.equeue.network.queue.QueueService
import ru.hse.equeue.network.settings.Singletons


class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val queueService: QueueService = Singletons.queueService

    var currentLocation = MutableLiveData<ListQueueRequest>()

    private var _queues = mutableListOf<Queue>()
    val queue: MutableList<Queue> = _queues

    var queuesResult = MutableLiveData<Result<List<Queue>>>()
    var pageQueueResult = MutableLiveData<Result<QueuePage>>()

    fun getQueues() {
        viewModelScope.launch {
            val result = queueService.getListQueue(currentLocation.value!!)
            queuesResult.value = result
        }
    }

    fun getPageQueues() {
        viewModelScope.launch {
            var page = 0
            if (pageQueueResult.value != null) {
                pageQueueResult.value?.onSuccess {
                    page = it.number + 1
                }
            }
            pageQueueResult.value =
                queueService.getListQueueByPage(currentLocation.value!!, page, 2)
        }
    }

    fun setQueues(newQueues: List<Queue>) {
        cleanQueues()
        _queues.addAll(newQueues)
    }

    fun cleanQueues() {
        for (i in _queues.indices) {
            _queues.removeAt(0)
        }
    }

    fun addQueues(newQueues: List<Queue>) {
        _queues.addAll(newQueues)
    }
}
package ru.hse.equeue.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.equeue.model.ListQueueRequest
import ru.hse.equeue.model.Queue
import ru.hse.equeue.model.QueuePage
import ru.hse.equeue.network.base.Result
import ru.hse.equeue.network.queue.QueueService
import ru.hse.equeue.network.settings.Singletons

data class MapPosition(
    var zoom: Float,
    var lat: Double,
    var lng: Double
)
class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val queueService: QueueService = Singletons.queueService

    var currentLocation = MutableLiveData<ListQueueRequest>()
    var mapPosition = MutableLiveData<MapPosition>()

    private var _queues = mutableListOf<Queue>()
    val queue: MutableList<Queue> = _queues

    var queuesResult = MutableLiveData<Result<List<Queue>>>()
    var searchQueuesResult = MutableLiveData<Result<QueuePage>>()
    var pageQueueResult = MutableLiveData<Result<QueuePage>>()
    var currentPage: Int = -1

    fun getQueues() {
        viewModelScope.launch {
            val result = queueService.getListQueue(currentLocation.value!!)
            queuesResult.value = result
        }
    }

    fun getPageQueues() {
        viewModelScope.launch {
            currentPage += 1
            pageQueueResult.value =
                queueService.getListQueueByPage(currentLocation.value!!, currentPage, 20)
        }
    }

    fun searchPageQueue(search: String) {
        viewModelScope.launch {
            searchQueuesResult.value =
                queueService.searchQueueByPage(currentLocation.value!!, 20, search)
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
        currentPage = -1
    }

    fun addQueues(newQueues: List<Queue>) {
        _queues.addAll(newQueues)
    }
}
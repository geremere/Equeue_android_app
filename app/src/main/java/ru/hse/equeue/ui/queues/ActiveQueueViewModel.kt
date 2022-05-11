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

    private val _queueResult = MutableLiveData<Result<Queue>>()
    private val _queue = MutableLiveData<Queue>()

    val queue: LiveData<Queue> = _queue
    val queueResult: LiveData<Result<Queue>> = _queueResult

    fun setQueue(queue: Queue) {
        _queue.value = queue
    }

    fun getActiveQueue(userId: String) {
        viewModelScope.launch {
            _queueResult.value = queueService.getQueueByUserId(userId)

        }
    }
}
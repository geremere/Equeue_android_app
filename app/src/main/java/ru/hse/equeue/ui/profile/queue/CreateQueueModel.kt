package ru.hse.equeue.ui.profile.queue

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.equeue.model.CreateQueue
import ru.hse.equeue.model.Queue
import ru.hse.equeue.network.base.Result
import ru.hse.equeue.network.queue.AddressesService
import ru.hse.equeue.network.queue.QueueService
import ru.hse.equeue.network.settings.Singletons
import java.io.File

class CreateQueueModel(application: Application) : AndroidViewModel(application) {

    private val addressesService: AddressesService = Singletons.addressesService
    private val queueService: QueueService = Singletons.queueService

    private val _queueResult = MutableLiveData<Result<Queue>>()

    val queue = CreateQueue()

    val queueResult: LiveData<Result<Queue>> = _queueResult

    fun createQueue(path: String) {
        if (queue.name != null && queue.address != null &&
            queue.status != null && queue.x != null &&
            queue.y != null && queue.owner != null &&
            queue.owner?.id != null
        ) {
            val file = File(path)
            viewModelScope.launch {
                val result = queueService.createQueue(queue, file)
                _queueResult.value = result
            }
        }
    }

    private val _addresses = MutableLiveData<List<String>>()

    val addresses: LiveData<List<String>> = _addresses

    fun getAddresses(text: String) {
        viewModelScope.launch {
            addressesService.getAddresses(text)
        }
    }

}

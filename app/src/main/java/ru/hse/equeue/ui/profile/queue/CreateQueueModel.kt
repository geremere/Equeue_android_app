package ru.hse.equeue.ui.profile.queue

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.equeue.model.CreateQueue
import ru.hse.equeue.network.queue.AddressesService
import ru.hse.equeue.network.queue.QueueService
import ru.hse.equeue.network.settings.Singletons
import java.io.File

class CreateQueueModel(application: Application) : AndroidViewModel(application) {

    private val addressesService: AddressesService = Singletons.addressesService
    private val queueService: QueueService = Singletons.queueService
    private val _queueCreated = MutableLiveData<Boolean>()


    private val _queue = MutableLiveData<CreateQueue>().apply {
        value = CreateQueue()
    }

    val queue: LiveData<CreateQueue> = _queue
    val queueCreated: LiveData<Boolean> = _queueCreated

    fun createQueue(path: String) {
        if (queue.value != null && queue.value?.name != null &&
            queue.value?.address != null && queue.value?.status != null &&
            queue.value?.x != null && queue.value?.y != null &&
            queue.value?.owner != null && queue.value?.owner?.id != null
        ) {
            val file = File(path)
            viewModelScope.launch {
                try {
                    val queue = queueService.createQueue(queue.value!!, file)
                    _queueCreated.value = true
                } catch (e: Exception) {
                    Log.i("EXCEPTION", e.message.toString())
                    _queueCreated.value = false
                }
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

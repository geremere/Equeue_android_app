package ru.hse.equeue.ui.search.queue

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.hse.equeue.model.Queue

class QueueViewModel(application: Application) : AndroidViewModel(application) {

    private val queueModel = MutableLiveData<Queue>()

    fun getQueue():Queue {
        return queueModel.value!!
    }

    fun setQueue(queue: Queue){
        queueModel.value = queue
    }
}
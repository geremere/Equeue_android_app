package ru.hse.equeue.ui.search.search_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.hse.equeue.model.Queue

class SearchListViewModel(application: Application) : AndroidViewModel(application)  {

    private var _queues = mutableListOf<Queue>()
    var search = MutableLiveData<String>()

    val queue: MutableList<Queue> = _queues

     fun setQueues(newQueues: List<Queue>) {
        cleanQueues()
        _queues.addAll(newQueues)
    }

    private fun cleanQueues() {
        for (i in _queues.indices) {
            _queues.removeAt(0)
        }
    }

}
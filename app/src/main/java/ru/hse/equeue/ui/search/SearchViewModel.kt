package ru.hse.equeue.ui.search

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import ru.hse.equeue.model.Queue
import ru.hse.equeue.model.QueueStatus
import java.util.*
import kotlin.random.Random


class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private var queues = mutableListOf<Queue>()

    init {
        IMAGES.shuffle()
        val geocoder = Geocoder(application.applicationContext, Locale.getDefault())
        val generatedQueues = (1..10).map {
            val x = 55.61332 + Random.nextDouble(0.0, .2)
            val y = 37.50336 + Random.nextDouble(0.0, .2)
            Queue(
                id = it.toLong(),
                photoUrl = IMAGES[it % IMAGES.size],
                name = "queue$it",
                x = x,
                y = y,
                averageWaitingTime = Random.nextDouble(100.0, 400.0),
                address = geocoder.getFromLocation(x, y, 1).get(0).getAddressLine(0),
                countOfPeople = Random.nextInt(1, 15),
                status = QueueStatus(12, 12, 12.2, "ACTIVE"),
                ownerId = "1"
            )
        }
        generatedQueues.forEach {
            queues.add(it)
        }
    }

    fun getQueues(): List<Queue> {
        return queues
    }

    companion object {
        private val IMAGES = mutableListOf(
            "https://avatars.mds.yandex.net/i?id=a01022d7c4a994e09e8e3b3826e615c3-4257150-images-thumbs&n=13",
            "https://avatars.mds.yandex.net/i?id=6e1e1d20eff6508284ad958b8b6e31e0-5429318-images-thumbs&n=13",
            "https://avatars.mds.yandex.net/i?id=2a00000179e443af2de7217b55efbd7607ce-4467639-images-thumbs&n=13",
            "https://avatars.mds.yandex.net/i?id=2a00000179f8d184a784494f1a4559fa7add-4474939-images-thumbs&n=13",
            "https://avatars.mds.yandex.net/i?id=0437d47540c4208911b48ca3530a8665-5869613-images-thumbs&n=13",
            "https://avatars.mds.yandex.net/i?id=e08226bdf2cacc59fb7d38e2a292b425-5875771-images-thumbs&n=13",
            "https://avatars.mds.yandex.net/i?id=615f575bbd2669aa65a7b6e9992815bd-4566166-images-thumbs&n=13"
        )
    }
}
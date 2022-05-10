package ru.hse.equeue.model

import kotlin.properties.Delegates

class CreateQueue {
     var name: String? = null
    var x :Double? = null
    var y :Double? = null
    var owner: User? = null
    var address: String? = null
    var status: QueueStatus? = null

    constructor(){

    }
}
package ru.hse.equeue.ui.profile

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.equeue.model.CreateQueue
import ru.hse.equeue.model.User
import ru.hse.equeue.network.queue.QueueService
import ru.hse.equeue.network.settings.Singletons
import ru.hse.equeue.network.user.UserService
import java.io.File

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userService: UserService = Singletons.userService

    private val _user = MutableLiveData<User>()




    val user: LiveData<User> = _user

    fun getUser() {
        viewModelScope.launch {
            val user: User = userService.getUser()
            _user.value = user
        }
    }
}
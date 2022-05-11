package ru.hse.equeue.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.equeue.model.User
import ru.hse.equeue.network.base.Result
import ru.hse.equeue.network.settings.Singletons
import ru.hse.equeue.network.user.UserService

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userService: UserService = Singletons.userService

    private val _userResponse = MutableLiveData<Result<User>>()

    private val _user = MutableLiveData<User>()

    fun setUser(user: User) {
        _user.value = user
    }

    val userResponse: LiveData<Result<User>> = _userResponse
    val user: LiveData<User> = _user

    fun getUser() {
        viewModelScope.launch {
            val result = userService.getUser()
            _userResponse.value = result
        }
    }
}
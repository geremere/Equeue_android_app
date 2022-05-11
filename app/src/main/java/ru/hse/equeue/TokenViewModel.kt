package ru.hse.equeue

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.equeue.network.auth.AuthService
import ru.hse.equeue.network.base.Result
import ru.hse.equeue.network.settings.Singletons

class TokenViewModel(
    application: Application
) : AndroidViewModel(application) {
    var token = MutableLiveData<Result<String>>()

    private val authService: AuthService = Singletons.authService

    fun getToken(idToken: String) {
        viewModelScope.launch {
            val jwt = authService.signIn(idToken)
            token.value = jwt
        }
    }
}
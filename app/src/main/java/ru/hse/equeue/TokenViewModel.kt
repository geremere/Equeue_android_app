package ru.hse.equeue

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.equeue.network.auth.AuthService
import ru.hse.equeue.network.settings.Singletons

class TokenViewModel(
    application: Application
) : AndroidViewModel(application) {
    var token = MutableLiveData<String>()

    private val authService: AuthService = Singletons.authService

    fun getToken(idToken: String) {
        viewModelScope.launch {
            val jwt = authService.signIn(idToken)
            Singletons.appSettings.setCurrentToken(jwt)
            token.value = jwt
        }
    }
}
package ru.hse.equeue.network.settings

import android.content.Context
import ru.hse.equeue.network.NetworkProviderHolder
import ru.hse.equeue.network.auth.AuthService
import ru.hse.equeue.network.base.OkHttpProvider
import ru.hse.equeue.network.queue.AddressesService
import ru.hse.equeue.network.queue.QueueService
import ru.hse.equeue.network.user.UserService

object Singletons {

    private lateinit var appContext: Context

    private val provider: OkHttpProvider by lazy {
        NetworkProviderHolder.provider
    }

    val appSettings: AppSettings by lazy {
        SharedPreferencesAppSettings(appContext)
    }

    // --- https services

    val authService: AuthService by lazy {
        provider.getAuthService()
    }
    val userService: UserService by lazy {
        provider.getUserService()
    }

    val addressesService: AddressesService by lazy{
        provider.getAddressesService()
    }

    val queueService:QueueService by lazy {
        provider.getQueueService()
    }

    /**
     * Call this method in all application components that may be created at app startup/restoring
     * (e.g. in onCreate of activities and services)
     */
    fun init(appContext: Context) {
        Singletons.appContext = appContext
    }
}

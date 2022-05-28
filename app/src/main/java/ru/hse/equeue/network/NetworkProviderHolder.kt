package ru.hse.equeue.network

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ru.hse.equeue.network.base.OkHttpConfig
import ru.hse.equeue.network.base.OkHttpProvider
import ru.hse.equeue.network.settings.AppSettings
import ru.hse.equeue.network.settings.Singletons
import java.util.concurrent.TimeUnit

object NetworkProviderHolder {

    val provider: OkHttpProvider by lazy<OkHttpProvider> {
        val config = OkHttpConfig(
            baseUrl = "https://equeue.hutsharing.com/api",
            client = createHttpClient(),
            gson = Gson()
        )
        OkHttpProvider(config)
    }

    private fun createHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(createAuthorizationInterceptor(Singletons.appSettings))
            .addInterceptor(createLoggingInterceptor())
            .build()
    }

    private fun createAuthorizationInterceptor(settings: AppSettings): Interceptor {
        return Interceptor { chain ->
            val newBuilder = chain.request().newBuilder()
            val token = settings.getCurrentToken()
            if (token != null) {
                newBuilder.addHeader("Authorization", "Bearer " + token)
            }
            return@Interceptor chain.proceed(newBuilder.build())
        }
    }

    /**
     * Log requests and responses to LogCat.
     */
    private fun createLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}
package com.flawlessrequests.network

import android.util.Log
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.gson.*

/**
 * Creates and configures a new instance of the Ktor [HttpClient].
 *
 * [Official docs](https://ktor.io/docs/create-client.html#close-client):
 * Note that **creating HttpClient is not a cheap operation**,
 * and it's better to **reuse (@Singleton)** its instance in the case of multiple requests.
 *
 * **Note:** You also need to call the [HttpClient.close] function when you're done with it
 * to free resources. If you need to use [HttpClient] for a single request,
 * call the [HttpClient.use] function, which automatically calls [HttpClient.close].
 *
 * @return a new pre-configured Ktor [HttpClient] instance.
 */
fun ktorClient(): HttpClient = HttpClient {
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Log.d("KTOR", message)
            }
        }
        level = LogLevel.ALL // logs everything
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 10_000 // 10s
        connectTimeoutMillis = 10_000 // 10s
    }

    install(ContentNegotiation) {
        gson(
            contentType = ContentType.Any // workaround for broken APIs
        )
    }
}
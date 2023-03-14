package com.flawlessrequests.network

import arrow.core.Either
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed interface HttpError {
    data class API(val response: HttpResponse) : HttpError
    data class Unknown(val exception: Exception) : HttpError
}

suspend inline fun <reified Data> httpRequest(
    ktorClient: HttpClient,
    crossinline request: suspend HttpClient.() -> HttpResponse
): Either<HttpError, Data> = withContext(Dispatchers.IO) {
    try {
        val response = request(ktorClient)
        if (response.status.isSuccess()) {
            // Success: 200 <= status code <= 299.
            Either.Right(response.body())
        } else {
            // Failure: unsuccessful status code.
            Either.Left(HttpError.API(response))
        }
    } catch (exception: Exception) {
        // Failure: exceptional, something wrong.
        Either.Left(HttpError.Unknown(exception))
    }
}
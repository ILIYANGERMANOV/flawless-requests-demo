package com.flawlessrequests.network.sample

import arrow.core.Either
import com.flawlessrequests.network.HttpError
import com.flawlessrequests.network.httpRequest
import com.google.gson.annotations.SerializedName
import io.ktor.client.*
import io.ktor.client.request.*

const val API_BASE = "https://www.myawesomeapi.com"

data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
)

data class LoginResponse(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("sessionToken")
    val sessionToken: String,
)

suspend fun HttpClient.login(request: LoginRequest): Either<HttpError, LoginResponse> =
    httpRequest(this) {
        post("$API_BASE/login") {
            setBody(request) // the body will be serialized to JSON
        }
    }
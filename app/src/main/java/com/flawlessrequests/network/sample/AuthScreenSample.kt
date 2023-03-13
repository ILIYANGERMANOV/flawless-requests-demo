package com.flawlessrequests.network.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.flawlessrequests.network.HttpError
import com.flawlessrequests.network.OperationFlow
import com.flawlessrequests.network.httpRequest
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject


data class Credentials(
    val username: String,
    val password: String,
)

data class AuthRequestBody(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
)

data class AuthResponseDto(
    @SerializedName("accessToken")
    val accessToken: String,
)

class AuthenticationRequest @Inject constructor(
    private val ktorClient: HttpClient
) : OperationFlow<Credentials, HttpError, AuthResponseDto>() {
    override suspend fun operation(
        input: Credentials
    ): Either<HttpError, AuthResponseDto> = httpRequest(ktorClient) {
        post("{API URL GOES HERE") {
            setBody(AuthRequestBody(input.username, input.password))
        }
    }
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRequest: AuthenticationRequest
) : ViewModel() {
    private val credentialsFlow = MutableStateFlow<Credentials?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val authFlow = credentialsFlow.flatMapLatest { credentials ->
        // the request will be send only when valid credentials are provided
        if (credentials != null)
            authRequest.flow(credentials) else flowOf(null)
    }

    fun retry() {
        viewModelScope.launch {
            // the request will be retried with the last valid credentials
            authRequest.retry()
        }
    }
}
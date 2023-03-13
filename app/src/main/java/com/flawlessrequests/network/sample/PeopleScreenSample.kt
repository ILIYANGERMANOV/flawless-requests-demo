package com.flawlessrequests.network.sample

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import arrow.core.Either
import com.flawlessrequests.network.Operation
import com.flawlessrequests.network.OperationFlow
import com.flawlessrequests.network.httpRequest
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import javax.inject.Inject


object PeopleError // Domain error
data class Person(
    // Domain type
    val names: String,
    val age: Int,
)

data class PersonDto(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("age")
    val age: Int,
)

data class PeopleResponse(
    @SerializedName("people")
    val people: List<PersonDto>
)

class PeopleRequest @Inject constructor(
    private val ktorClient: HttpClient,
) : OperationFlow<Unit, PeopleError, List<Person>>() {
    override suspend fun operation(input: Unit): Either<PeopleError, List<Person>> =
        httpRequest<PeopleResponse>(ktorClient) {
            get("{PEOPLE_API_URL}")
        }.mapLeft { httpError ->
            PeopleError // map HttpError to domain error
        }.map { response ->
            // map Response (DTO) to domain
            response.people.map { dto ->
                Person(
                    names = listOfNotNull(dto.firstName, dto.lastName).joinToString(" "),
                    age = dto.age,
                )
            }
        }
}

@HiltViewModel
class PeopleViewModel @Inject constructor(
    private val peopleRequest: PeopleRequest
) : ViewModel() {
    val opPeopleFlow = peopleRequest.flow(Unit)

    fun retryPeopleRequest() {
        viewModelScope.launch {
            peopleRequest.retry()
        }
    }
}

@Composable
fun PeopleScreen() {
    val viewModel: PeopleViewModel = viewModel()
    val opPeople by viewModel.opPeopleFlow.collectAsState(Operation.Loading)

    when (opPeople) {
        is Operation.Error -> {
            Button(onClick = {
                viewModel.retryPeopleRequest()
            }) {
                Text(text = "Error! Tap to retry.")
            }
        }
        Operation.Loading -> {
            // Loading state
        }
        is Operation.Ok -> {
            // Success state
        }
    }
}
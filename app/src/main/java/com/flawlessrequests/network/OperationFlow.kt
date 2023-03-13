package com.flawlessrequests.network

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

/**
 * Transforms a **potentially long-running operation that can result in [Either] success or error**
 * to a [Flow]<[Operation]> that'll automatically emit [Operation.Loading] before the operation
 * is started and provide an out of the box [OperationFlow.retry] capabilities.
 *
 * **Usage:**
 * 1) Extend [OperationFlow].
 * 2) Implement (override) [OperationFlow.operation] :: [Input] -> Either<[Err], [Data]>.
 * 3) Call [OperationFlow.flow] ([Input]) to trigger a [Flow] of
 * [Operation.Loading] -> [Operation.Ok]/[Operation.Error].
 * 4) In case of an error, you can retry the [Operation] by calling [OperationFlow.retry].
 */
abstract class OperationFlow<in Input, out Err, out Data> {
    protected abstract suspend fun operation(input: Input): Either<Err, Data>

    /**
     * Used to trigger the [operation] execution.
     */
    private val triggerFlow = MutableSharedFlow<Unit>(
        // trigger the first operation() execution when flow(Input) is called later
        replay = 1,
    )

    init {
        // trigger the first operation() execution when flow(Input) is called later
        triggerFlow.tryEmit(Unit)
    }

    /**
     * Creates a flow that'll immediately execute the [OperationFlow.operation].
     * @param input input that will be supplied to the [OperationFlow.operation]
     * @return a new [Flow]<[Operation]> with the supplied [Input].
     */
    fun flow(input: Input): Flow<Operation<Err, Data>> = channelFlow {
        // "channelFlow" because we may collect from different coroutines
        triggerFlow.collectLatest {
            send(Operation.Loading)
            send(
                when (val result = operation(input)) {
                    is Either.Left -> Operation.Error(result.value)
                    is Either.Right -> Operation.Ok(result.value)
                }
            )
        }
    }

    /**
     * Makes the [OperationFlow.flow] re-execute the operation with the last supplied [Input].
     * If the [OperationFlow.flow] isn't collected, nothing will happen.
     */
    suspend fun retry() {
        triggerFlow.emit(Unit)
    }
}
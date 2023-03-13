package com.flawlessrequests.network

/**
 * Defines a potentially long-running operation that:
 * 1) Must have a [Operation.Loading] state
 * 2) Results either in [Operation.Ok] or [Operation.Error]
 *
 * _Example: A good use-case for an [Operation] is sending HTTP requests to a server._
 */
sealed interface Operation<out Err, out Data> {
    /**
     * Loading state.
     */
    object Loading : Operation<Nothing, Nothing>

    /**
     * Success state with [Data].
     */
    data class Ok<out Data>(val data: Data) : Operation<Nothing, Data>

    /**
     * Error state with [Err].
     */
    data class Error<out Err>(val error: Err) : Operation<Err, Nothing>
}

/**
 * Transforms [Operation.Ok] case using the [transform] lambda.
 * Note: [Operation.Loading] and [Operation.Error] remain unchanged.
 * @param transform transformation (mapping) function for the [Operation.Ok]'s data.
 * @return a new [Operation] with transformed [Operation.Ok] case.
 */
fun <E, D1, D2> Operation<E, D1>.mapSuccess(
    transform: (D1) -> D2
): Operation<E, D2> = when (this) {
    is Operation.Error -> this
    is Operation.Loading -> this
    is Operation.Ok -> Operation.Ok(
        data = transform(this.data)
    )
}

/**
 * Transforms [Operation.Error] case using the [transform] lambda.
 * Note: [Operation.Loading] and [Operation.Ok] remain unchanged.
 * @param transform transformation (mapping) function for the [Operation.Error]'s error.
 * @return a new [Operation] with transformed [Operation.Error] case.
 */
fun <E, E2, D> Operation<E, D>.mapError(
    transform: (E) -> E2
): Operation<E2, D> = when (this) {
    is Operation.Error -> Operation.Error(
        error = transform(this.error)
    )
    is Operation.Loading -> this
    is Operation.Ok -> this
}
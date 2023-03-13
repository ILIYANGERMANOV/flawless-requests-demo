package com.flawlessrequests.network.sample

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import com.flawlessrequests.network.HttpError

@JvmInline
value class TrackingId(val id: String)

suspend fun getOrderId(): Either<HttpError, String> = TODO()
suspend fun confirmOrder(orderId: String): Either<HttpError, Boolean> = TODO()
suspend fun trackOrder(orderId: String): Either<HttpError, TrackingId> = TODO()

suspend fun placeOrderChain(): Either<HttpError, TrackingId?> = either {
    val orderId = getOrderId().bind()
    val canBeTracked = confirmOrder(orderId).bind()
    if (canBeTracked) {
        trackOrder(orderId).bind()
    } else {
        null
    }
}

suspend fun placeOrderFlatMap(): Either<HttpError, TrackingId?> =
    getOrderId().flatMap { orderId ->
        confirmOrder(orderId).flatMap { canBeTracked ->
            if (canBeTracked) {
                trackOrder(orderId).flatMap { trackingId ->
                    Either.Right(trackingId)
                }
            } else Either.Right(null)
        }
    }
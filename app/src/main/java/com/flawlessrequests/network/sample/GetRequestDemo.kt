package com.flawlessrequests.network.sample

import arrow.core.Either
import com.flawlessrequests.network.HttpError
import com.flawlessrequests.network.httpRequest
import com.flawlessrequests.network.ktorClient
// Depends on: 'com.google.code.gson:gson' if you're using GSON
import com.google.gson.annotations.SerializedName
import io.ktor.client.request.*

val ktorSingleton = ktorClient()
const val PRODUCTS_PER_PAGE = 24

data class ProductsResponse(
    @SerializedName("products")
    val products: List<Product>
)

data class Product(
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Double,
)

// Imaginary API
suspend fun fetchProductsFromAPI(page: Int): Either<HttpError, ProductsResponse> =
    httpRequest(ktorSingleton) {
        get("https://www.awesomeapi.com/prodcuts") {
            headers {
                set("API_KEY", "{YOUR_API_KEY}")
            }

            parameter("offset", page * PRODUCTS_PER_PAGE)
            parameter("limit", PRODUCTS_PER_PAGE)
        }
    }

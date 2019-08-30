package com.attyran.flickrsearch

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface BackendClient {
    @GET("rest/?&method=flickr.photos.search&api_key=$API_KEY&format=json&nojsoncallback=1")
    fun search(@Query("tags") tag: String): Single<PhotoSearchResponse>

    companion object {
        const val API_KEY = "1508443e49213ff84d566777dc211f2a"
        const val BASE_URL = "https://api.flickr.com/services/"
    }
}
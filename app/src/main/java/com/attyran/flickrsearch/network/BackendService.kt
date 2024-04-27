package com.attyran.flickrsearch.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface BackendService {

    @GET("rest/?&method=flickr.photos.search&api_key=$API_KEY&format=json&nojsoncallback=1")
    suspend fun search(@Query("tags") tag: String): PhotoResponse

    companion object {
        private const val API_KEY = "1508443e49213ff84d566777dc211f2a"
        private const val BASE_URL = "https://api.flickr.com/services/"

        fun create(): BackendService {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(BackendService::class.java)
        }
    }
}
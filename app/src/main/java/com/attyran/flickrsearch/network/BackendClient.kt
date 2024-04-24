package com.attyran.flickrsearch.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BackendClient {

    private val apiService: BackendService by lazy {
        val apiService = Retrofit.Builder()
            .baseUrl(BackendService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService.create(BackendService::class.java)
    }

    suspend fun getPhotos(tag: String): PhotoSearchResponse {
        return apiService.search(tag)
    }
}
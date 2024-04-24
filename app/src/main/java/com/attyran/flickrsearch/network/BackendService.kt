package com.attyran.flickrsearch.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class BackendService {

    private val apiService: BackendClient by lazy {
        val apiService = Retrofit.Builder()
            .baseUrl(BackendClient.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        apiService.create(BackendClient::class.java)
    }

    suspend fun getPhotos(tag: String): PhotoSearchResponse {
        return apiService.search(tag)
    }
}
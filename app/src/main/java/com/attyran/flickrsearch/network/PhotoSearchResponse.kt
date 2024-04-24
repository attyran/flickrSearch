package com.attyran.flickrsearch.network

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class PhotoSearchResponse(
    @Json(name = "photos") val photos: Result?
)

@JsonClass(generateAdapter = true)
data class Result(
    @Json(name = "photo") val photo: List<Photo> = emptyList()
)
package com.attyran.flickrsearch.network

import com.google.gson.annotations.SerializedName

data class PhotoSearchResponse(
    @SerializedName("photos") val photos: Result?
)

data class Result(
    @SerializedName("photo") val photo: List<Photo> = emptyList()
)
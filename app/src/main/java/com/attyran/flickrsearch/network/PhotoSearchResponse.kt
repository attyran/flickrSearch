package com.attyran.flickrsearch.network

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class PhotoResponse(
    @Json(name = "photos") val photos: Photos,
    @Json(name = "stat") val stat: String
)

@JsonClass(generateAdapter = true)
data class Photos(
    @Json(name = "page") val page: Int,
    @Json(name = "pages") val pages: Int,
    @Json(name = "perpage") val perPage: Int,
    @Json(name = "total") val total: Int,
    @Json(name = "photo") val photo: List<PhotoItem>
)
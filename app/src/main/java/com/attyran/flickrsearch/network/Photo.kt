package com.attyran.flickrsearch.network
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoItem(
    @Json(name = "id") val id: String,
    @Json(name = "owner") val owner: String,
    @Json(name = "secret") val secret: String,
    @Json(name = "server") val server: String,
    @Json(name = "farm") val farm: String,
    @Json(name = "title") val title: String,
    @Json(name = "ispublic") val isPublic: String,
    @Json(name = "isfriend") val isFriend: String,
    @Json(name = "isfamily") val isFamily: String
)
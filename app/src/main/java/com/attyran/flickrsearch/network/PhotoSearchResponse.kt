package com.attyran.flickrsearch.network

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

/**
 * Represents the top-level API response from the Flickr photo search endpoint.
 */
@JsonClass(generateAdapter = true)
data class PhotoResponse(
    @Json(name = "photos") val photos: Photos,
    @Json(name = "stat") val stat: String
)

/**
 * Contains pagination details and the list of retrieved photo items from the Flickr search results.
 */
@JsonClass(generateAdapter = true)
data class Photos(
    @Json(name = "page") val page: Int,
    @Json(name = "pages") val pages: Int,
    @Json(name = "perpage") val perPage: Int,
    @Json(name = "total") val total: Int,
    @Json(name = "photo") val photo: List<PhotoItem>
)
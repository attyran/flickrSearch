package com.attyran.flickrsearch

import com.attyran.flickrsearch.network.Photo
import com.attyran.flickrsearch.network.PhotoSearchResponse
import com.attyran.flickrsearch.network.Result

val mockPhotos = listOf(
    Photo("1234", "mock_owner", "mock_secret", "mock_server", "mock_farm", "mock_title", "mock_ispublic", "mock_isfriend", "mock_isfamily"),
    Photo("5678", "mock_owner", "mock_secret", "mock_server", "mock_farm", "mock_title", "mock_ispublic", "mock_isfriend", "mock_isfamily"),
    Photo("1357", "mock_owner", "mock_secret", "mock_server", "mock_farm", "mock_title", "mock_ispublic", "mock_isfriend", "mock_isfamily"))

fun createMockPhotoSearchResponse(photos: List<Photo> = ArrayList()) : PhotoSearchResponse{
    return PhotoSearchResponse(Result(photos))
}
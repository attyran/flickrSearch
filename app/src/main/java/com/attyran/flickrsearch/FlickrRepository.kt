package com.attyran.flickrsearch

import com.attyran.flickrsearch.network.BackendService
import com.attyran.flickrsearch.network.PhotoItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlickrRepository @Inject constructor(
    private val backendService: BackendService
) {
    suspend fun searchTag(tag: String): Result<List<PhotoItem>> {
        return kotlin.runCatching { backendService.search(tag) }
            .mapCatching { response ->
                if (response.stat != "ok" || response.photos.photo.isEmpty()) {
                    throw Exception("No photos found")
                } else {
                    response.photos.photo
                }
            }
    }
}
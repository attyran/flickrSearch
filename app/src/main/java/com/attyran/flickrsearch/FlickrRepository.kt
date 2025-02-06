package com.attyran.flickrsearch

import com.attyran.flickrsearch.network.BackendService
import com.attyran.flickrsearch.network.PhotoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlickrRepository @Inject constructor(
    private val backendService: BackendService
) {
    fun searchTag(tag: String): Flow<Result<List<PhotoItem>>> = flow {
        emit(
            kotlin.runCatching { backendService.search(tag) }
                .mapCatching { response ->
                    if (response.stat != "ok" || response.photos.photo.isEmpty()) {
                        throw Exception("No photos found")
                    } else {
                        response.photos.photo
                    }
                }
        )
    }
}
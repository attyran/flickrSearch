package com.attyran.flickrsearch

import com.attyran.flickrsearch.network.BackendService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlickrRepository @Inject constructor(
    private val backendService: BackendService
) {
    private val _flickrState = MutableStateFlow<FlickrUiState>(FlickrUiState.Idle)
    val flickrState: StateFlow<FlickrUiState> = _flickrState

    suspend fun searchTag(tag: String) {
        val result = kotlin.runCatching { backendService.search(tag) }
        result.onSuccess { response ->
            if (response.stat != "ok" || response.photos.photo.isEmpty()) {
                _flickrState.value = FlickrUiState.Error("No photos found")
            } else {
                _flickrState.value = FlickrUiState.Success(response.photos.photo)
            }
        }
        result.onFailure { throwable ->
            _flickrState.value = FlickrUiState.Error(throwable.message ?: "An error occurred")
        }
    }
}
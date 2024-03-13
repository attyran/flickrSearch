package com.attyran.flickrsearch

import androidx.lifecycle.ViewModel
import com.attyran.flickrsearch.network.BackendService
import com.attyran.flickrsearch.network.PhotoSearchResponse
import com.attyran.flickrsearch.network.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FlickrViewModel : ViewModel() {

    private val _photoState: MutableStateFlow<PhotoSearchResponse> =
        MutableStateFlow(PhotoSearchResponse(Result(ArrayList())))
    val photoState: StateFlow<PhotoSearchResponse> = _photoState

    private val backendService = BackendService()

    suspend fun search(tag: String) {
        val response = backendService.getPhotos(tag)
        return if (response.photos.photo.isNotEmpty()) {
            _photoState.value = response
        } else {
            _photoState.value = PhotoSearchResponse(Result(ArrayList()))
        }
    }
}
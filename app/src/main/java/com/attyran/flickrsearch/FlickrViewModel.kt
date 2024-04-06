package com.attyran.flickrsearch

import androidx.lifecycle.ViewModel
import com.attyran.flickrsearch.network.BackendService
import com.attyran.flickrsearch.network.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FlickrViewModel(private val backendService: BackendService) : ViewModel() {

    private val _photoState = MutableStateFlow<UIState>(UIState.Error(""))
    val photoState: StateFlow<UIState> = _photoState

    suspend fun search(tag: String) {
        try {
            val result = backendService.getPhotos(tag).photos
            if (result == null) {
                _photoState.value = UIState.Error("No photos found")
                return
            }
            _photoState.value = UIState.Success(result.photo)
        } catch (e: Exception) {
            _photoState.value = UIState.Error(e.message ?: "An error occurred")
        }
    }


    sealed class UIState {
        data class Success(val photos: List<Photo>) : UIState()
        data class Error(val message: String) : UIState()
    }
}
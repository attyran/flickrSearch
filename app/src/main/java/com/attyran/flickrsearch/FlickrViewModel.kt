package com.attyran.flickrsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attyran.flickrsearch.network.BackendClient
import com.attyran.flickrsearch.network.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FlickrViewModel(private val backendClient: BackendClient) : ViewModel() {

    private val _photoState = MutableStateFlow<UIState>(UIState.Error(""))
    val photoState: StateFlow<UIState> = _photoState

    fun searchTag(tag: String) {
        viewModelScope.launch {
            try {
                val result = backendClient.getPhotos(tag).photos
                if (result == null) {
                    _photoState.value = UIState.Error("No photos found")
                    return@launch
                }
                _photoState.value = UIState.Success(result.photo)
            } catch (e: Exception) {
                _photoState.value = UIState.Error(e.message ?: "An error occurred")
            }
        }
    }

    sealed class UIState {
        data class Success(val photos: List<Photo>) : UIState()
        data class Error(val message: String) : UIState()
    }
}
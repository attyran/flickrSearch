package com.attyran.flickrsearch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attyran.flickrsearch.network.BackendService
import com.attyran.flickrsearch.network.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlickrViewModel @Inject constructor(
    private val backendService: BackendService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _photoState = MutableStateFlow<UIState>(UIState.Error(""))
    val photoState: StateFlow<UIState> = _photoState

    private val _listState = MutableStateFlow<List<String>>(emptyList())
    val listState: StateFlow<List<String>> = _listState

    fun searchTag(tag: String) {
        viewModelScope.launch {
            try {
                val result = backendService.getPhotos(tag).photos
                if (result == null) {
                    _photoState.value = UIState.Error("No photos found")
                    return@launch
                }
                _photoState.value = UIState.Success(result.photo)
                _listState.value = result.photo.map { photo ->
                    String.format(
                        "https://farm%s.staticflickr.com/%s/%s_%s.jpg",
                        photo.farm, photo.server, photo.id, photo.secret
                    )
                }
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
package com.attyran.flickrsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attyran.flickrsearch.network.BackendService
import com.attyran.flickrsearch.network.PhotoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlickrViewModel @Inject constructor(
    private val backendService: BackendService
) : ViewModel() {

    private val _photoState = MutableStateFlow<UIState>(UIState.Error(""))
    val photoState: StateFlow<UIState> = _photoState

    fun searchTag(tag: String) {
        viewModelScope.launch {
            try {
                val result = backendService.search(tag)

                if (result.stat != "ok") {
                    _photoState.value = UIState.Error("No photos found")
                    return@launch
                }
                _photoState.value = UIState.Success(result.photos.photo)
            } catch (e: Exception) {
                _photoState.value = UIState.Error(e.message ?: "An error occurred")
            }
        }
    }

    sealed class UIState {
        data class Success(val photos: List<PhotoItem>) : UIState()
        data class Error(val message: String) : UIState()
    }
}
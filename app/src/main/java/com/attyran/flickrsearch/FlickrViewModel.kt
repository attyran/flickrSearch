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
    private val repository: FlickrRepository
) : ViewModel() {
    private val _photoState = MutableStateFlow<FlickrUiState>(FlickrUiState.Idle)
    val photoState: StateFlow<FlickrUiState> = _photoState

    fun searchTag(tag: String) {
        viewModelScope.launch {
            _photoState.value = FlickrUiState.Idle
            val result = repository.searchTag(tag)
            result.onSuccess { photos ->
                _photoState.value = FlickrUiState.Success(photos)
            }
            result.onFailure { throwable ->
                _photoState.value = FlickrUiState.Error(throwable.message ?: "An error occurred")
            }
        }
    }
}
package com.attyran.flickrsearch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * ViewModel responsible for managing search requests, handling state transitions
 * of the Flickr search UI, and caching paginated photo search results.
 */
@HiltViewModel
class FlickrViewModel @Inject constructor(
    private val repository: FlickrRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<FlickrContract.UiState>(FlickrContract.UiState.Idle)
    val uiState: StateFlow<FlickrContract.UiState> = _uiState

    fun processIntent(intent: FlickrContract.Intent) {
        when (intent) {
            is FlickrContract.Intent.Search -> searchTag(intent.query)
        }
    }

    private fun searchTag(tag: String) {
        if (tag.isBlank()) {
            Log.d("FlickrViewModel", "Search query is blank")
            _uiState.value = FlickrContract.UiState.Error("Search query is blank")
            return
        }

        Log.d("FlickrViewModel", "Searching for tag: $tag")
        val pagingFlow = repository.searchTag(tag).cachedIn(viewModelScope)
        _uiState.value = FlickrContract.UiState.Success(pagingFlow)
    }
}

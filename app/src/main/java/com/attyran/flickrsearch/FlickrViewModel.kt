package com.attyran.flickrsearch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.attyran.flickrsearch.network.PhotoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing search requests, handling state transitions
 * of the Flickr search UI, and caching paginated photo search results.
 */
@HiltViewModel
class FlickrViewModel @Inject constructor(
    private val repository: FlickrRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<FlickrUiState>(FlickrUiState.Idle)
    val uiState: StateFlow<FlickrUiState> = _uiState

    private val _searchResults = MutableStateFlow(PagingData.empty<PhotoItem>())
    val searchResults: StateFlow<PagingData<PhotoItem>> = _searchResults

    private var currentQuery: String? = null

    fun searchTag(tag: String) {
        if (tag.isBlank()) {
            Log.d("FlickrViewModel", "Search query is blank")
            _uiState.value = FlickrUiState.Error("Search query is blank")
            return
        }

        if (tag == currentQuery) {
            Log.d("FlickrViewModel", "Same search query as before: $tag")
            return
        }

        Log.d("FlickrViewModel", "Searching for tag: $tag")
        currentQuery = tag
        _uiState.value = FlickrUiState.Loading
        viewModelScope.launch {
            repository.searchTag(tag)
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _searchResults.value = pagingData
                }
        }
    }

    fun onRefreshLoadState(state: LoadState) {
        if (currentQuery == null) return
        _uiState.value = when (state) {
            is LoadState.Loading -> FlickrUiState.Loading
            is LoadState.NotLoading -> FlickrUiState.Success
            is LoadState.Error -> FlickrUiState.Error(
                state.error.message ?: "Failed to load photos"
            )
        }
    }
}

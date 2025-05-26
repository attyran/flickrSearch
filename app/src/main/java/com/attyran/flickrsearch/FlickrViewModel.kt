package com.attyran.flickrsearch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
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
    private val _searchResults = MutableStateFlow<PagingData<PhotoItem>>(PagingData.empty())
    val searchResults: StateFlow<PagingData<PhotoItem>> = _searchResults
    private var currentQuery: String? = null

    fun searchTag(tag: String) {
        if (tag.isBlank()) {
            Log.d("FlickrViewModel", "Search query is blank")
            return
        }
        
        if (tag == currentQuery) {
            Log.d("FlickrViewModel", "Same search query as before: $tag")
            return
        }

        Log.d("FlickrViewModel", "Searching for tag: $tag")
        currentQuery = tag
        viewModelScope.launch {
            repository.searchTag(tag)
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _searchResults.value = pagingData
                }
        }
    }
}